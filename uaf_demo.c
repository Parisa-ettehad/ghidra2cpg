extern void *malloc(unsigned long size);
extern void free(void *p);

void sink(void *p) {
    // dummy function for the demo
}

void bad() {
    int *v1 = (int *)malloc(5 * sizeof(int));
    int *v2 = (int *)malloc(5 * sizeof(int));

    free(v1);
    sink(v1);   // UAF
    free(v2);
}

void good() {
    int *v1 = (int *)malloc(5 * sizeof(int));
    int *v2 = (int *)malloc(5 * sizeof(int));

    free(v1);
    sink(v2);   // not UAF for v1
    free(v2);
}

int main() {
    bad();
    good();
    return 0;
}