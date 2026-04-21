#include <stdlib.h>

void bad() {
    int *p = malloc(sizeof(int));
    free(p);
    *p = 5;
}

void good() {
    int *p = malloc(sizeof(int));
    *p = 7;
    free(p);
}

int main() {
    bad();
    good();
    return 0;
}
