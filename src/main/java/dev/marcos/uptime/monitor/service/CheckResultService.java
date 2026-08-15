package dev.marcos.uptime.monitor.service;

import dev.marcos.uptime.monitor.dto.response.CheckResultSummaryResponse;
import dev.marcos.uptime.monitor.dto.response.UptimePercentageResponse;
import dev.marcos.uptime.monitor.exceptions.MonitorNotFoundException;
import dev.marcos.uptime.monitor.model.CheckResult;
import dev.marcos.uptime.monitor.model.Monitor;
import dev.marcos.uptime.monitor.model.User;
import dev.marcos.uptime.monitor.model.UserDetailsImpl;
import dev.marcos.uptime.monitor.repository.CheckResultRepository;
import dev.marcos.uptime.monitor.repository.MonitorRepository;
import dev.marcos.uptime.monitor.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class CheckResultService {
    private final CheckResultRepository repository;
    private final UserRepository userRepository;
    private final MonitorRepository monitorRepository;

    public CheckResultService(CheckResultRepository repository, UserRepository userRepository, MonitorRepository monitorRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.monitorRepository = monitorRepository;
    }

    private User getUserInContext(){
        var context = (UserDetailsImpl) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        try {
            return userRepository.findByUsername(context.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Username " + context.getUsername() + " not found"));
        }catch (UsernameNotFoundException e){
            log.warn("Username {} not found", context.getUsername());
            throw e;
        }
    }
    private Monitor getMonitorWithIDORPrevention(Long id){
        Monitor monitor;
        try {
             monitor = monitorRepository.findById(id)
                    .orElseThrow(() -> new MonitorNotFoundException("Monitor not found."));
        }catch (MonitorNotFoundException e){
            log.warn("Monitor {} not found.", id);
            throw e;
        }
        User userInContext = getUserInContext();
        if (!monitor.getOwner().getId().equals(userInContext.getId())) {
            log.warn("IDOR attempt: user {} tried to acess monitor owned by {}", userInContext.getId(), monitor.getOwner().getId());
            throw new AccessDeniedException("User " + userInContext.getUsername() + " are not permitted for this request.");
        }
        return monitor;
    }


    public List<CheckResultSummaryResponse> getMonitorCheckHistory(Long monitorId, Integer pageNumber){
        log.info("Getting check history with id {} ", monitorId);
        Monitor monitor = getMonitorWithIDORPrevention(monitorId);
        Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by(Sort.Direction.DESC , "checkedAt"));
        List<CheckResultSummaryResponse> responses = new ArrayList<>();
        Page<CheckResult> page = repository.findByMonitor(monitor, pageable);
        for (CheckResult checkResult : page.getContent()) {
            responses.add(
                    new CheckResultSummaryResponse(
                            checkResult.getCheckedAt(),
                            checkResult.getHttpStatus(),
                            checkResult.getResponseTimeMs(),
                            checkResult.getSuccess(),
                            checkResult.getErrorMessage()
                    )
            );
        }
        return  responses;
        }
        public UptimePercentageResponse getPercentOfSuccessInPeriod(Instant startDate, Instant endDate, Long monitorId) {
            log.info("Getting uptime percentage for monitor {} from {} to {}", monitorId, startDate, endDate);
        Monitor monitor = this.getMonitorWithIDORPrevention(monitorId);
        Double percentOfSuccess = repository.findAverageBySuccess(startDate,endDate,monitor);
        return new UptimePercentageResponse(percentOfSuccess);
        }

    }
