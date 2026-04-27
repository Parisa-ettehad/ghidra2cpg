void *malloc(unsigned long size);
void free(void *ptr);

void sink(int *p) {
    *p = 42;
}

void bad_uaf() {
    int *v1 = (int *)malloc(5 * sizeof(int));
    free(v1);
    sink(v1);   
}

void good_different_pointer() {
    int *v1 = (int *)malloc(5 * sizeof(int));
    int *v2 = (int *)malloc(5 * sizeof(int));

    free(v1);
    sink(v2);   
    free(v2);
}


void good_stack_pointer() {
    int x = 5;
    int *v1 = (int *)malloc(5 * sizeof(int));

    free(v1);
    sink(&x);   
}

int main() {
    return 0;
}