all:
	$(NASM) -f elf integrate.asm -o integrate.o
	$(CC) -shared -o libnative.so *.c integrate.o

clean:
	rm *.so *.o || true
