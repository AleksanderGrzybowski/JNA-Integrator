package org.kelog.japroj.web;

import com.google.common.collect.Range;
import lombok.RequiredArgsConstructor;
import org.kelog.japroj.impl.JavaIntegrator;
import org.kelog.japroj.value.IntegrationResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@CrossOrigin
@RequiredArgsConstructor
public class AppController {
    
    private static final int THREAD_COUNT = 4;
    private final JavaIntegrator integrator;
    
    @RequestMapping("calculate")
    @ResponseBody
    public IntegrationResult calculate(
            @RequestParam("left") Double left,
            @RequestParam("right") Double right,
            @RequestParam("func") String function,
            @RequestParam("numberOfPoints") Integer numberOfPoints
    ) {
        
        return integrator.integrate(Range.closed(left, right), numberOfPoints, function, THREAD_COUNT);
    }
}
