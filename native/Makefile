all:
	nasm -f elf integrate.asm -o integrate.o
	gcc -shared -o libnative.so *.c integrate.o


clean:
	rm *.so
