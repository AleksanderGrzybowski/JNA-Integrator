package org.kelog.japroj;

import com.google.common.collect.Range;
import org.kelog.japroj.core.Integrator;
import org.kelog.japroj.impl.JavaIntegrator;
import org.kelog.japroj.value.IntegrationResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@CrossOrigin
public class JnaController {
    
    
    @RequestMapping("calculate")
    @ResponseBody
    public IntegrationResult calculate(
            @RequestParam("left") Double left,
            @RequestParam("right") Double right,
            @RequestParam("func") String func,
            @RequestParam("numberOfPoints") Integer numberOfPoints
    ) {
        
        Integrator integrator = new JavaIntegrator();
        
        String function;
        
        Range<Double> range = Range.closed(
                left,
                right
        );
        function = func;
    
        try {
            return integrator.integrate(range, numberOfPoints, function, 4);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
