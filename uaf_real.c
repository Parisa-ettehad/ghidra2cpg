#include <stdlib.h>

void bad() {
    int *p = malloc(sizeof(int));
    free(p);
    *p = 10; // use-after-free
}

void good() {
    int *p = malloc(sizeof(int));
    *p = 20;
    free(p); // safe
}

int main() {
    bad();
    good();
    return 0;
}