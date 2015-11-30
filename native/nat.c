#include <stdio.h>

double integrateC(double left, double right, int points, double* values) {
    double result = values[0] + values[points];

    int i;
    for (i = 1; i <= (points-1); ++i)
        result += 2*values[i];

    result *= ( ((double)(right-left))/(2.0*points));
    return result;
}