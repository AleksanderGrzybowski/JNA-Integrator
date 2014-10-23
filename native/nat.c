#include <stdio.h>



double integrateC(double left, double right, int points, double* wartosci) {
	double wynik = wartosci[0] + wartosci[points];

	int i;
	for (i = 1; i <= (points-1); ++i)
		wynik += 2*wartosci[i];
	

	wynik *= ( ((double)(right-left))/(2.0*points));

	return wynik;
}

double integrate_debug(double left, double right, int points, double* wartosci) {

	printf("left=%f right=%f points=%d\n", left, right, points);

	int x;
	// for (x = 0; x <= points; ++x) {
	// 	printf("Pod pozycją %d jest liczba %lf\n", x, wartosci[x]);
	// }


	double wynik = 0;

	// wartości: <0..n>

	wynik += wartosci[0] + wartosci[points];

	int i;
	for (i = 1; i <= (points-1); ++i)
		wynik += 2*wartosci[i];

	

	wynik *= ( ((double)(right-left))/(2.0*points));
	printf("%f\n", wynik);
	return wynik;
}
