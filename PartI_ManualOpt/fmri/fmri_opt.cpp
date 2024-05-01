#include <assert.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <unistd.h>

#define N 10000
int64_t a[N];
int64_t b[N];
int64_t c[N];
int64_t temp[N];

#define TIME
#ifdef TIME
#define IF_TIME(foo) foo;
#else
#define IF_TIME(foo)
#endif

void init_array() {
  int i, j;

  for (i = 0; i < N; i++) {
    a[i] = 0;
    temp[i] = 0;
    b[i] = i;
    c[i] = i * i;
  }
}

void print_array() {
  int i, j;

  for (i = 0; i < N; i++) {
    fprintf(stderr, "%lu ", a[i]);
    if (j % 80 == 79)
      fprintf(stderr, "\n");
  }
  fprintf(stderr, "\n");
}

double rtclock() {
  struct timezone Tzp;
  struct timeval Tp;
  int stat;
  stat = gettimeofday(&Tp, &Tzp);
  if (stat != 0)
    printf("Error return from gettimeofday: %d", stat);
  return (Tp.tv_sec + Tp.tv_usec * 1.0e-6);
}
double t_start, t_end;

int main() {
  int i, j, k;

  init_array();

  int iter = 10000 / N;
  if (iter < 1)
    iter = 1;

  IF_TIME(t_start = rtclock());
  

  // Optimized
#pragma scop

  for (i = 0; i < N; i++) {
	for (j = 0; j <= i; j++) {
	  temp[i] += b[i] * c[j];
	}
	a[i] += temp[i] * iter;
  }
#pragma endscop
  int64_t rst_org = a[N - 1];

  IF_TIME(t_end = rtclock());
  IF_TIME(printf("time %0.6lfs\n", t_end - t_start));

#ifdef TEST
  print_array();
#endif
  printf("result: %lu\n", rst_org);
  return 0;
}
