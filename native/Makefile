all:
	$(NASM) -f elf integrate.asm -o integrate.o
	$(CC) -shared -o libnative.so *.c integrate.o -fPIC -m32

clean:
	rm *.so *.o || true
