void g(int x) {
     
}

int f(int x, int y) requires x>=0 && y>0 {
    int z;
    g(z);
}
