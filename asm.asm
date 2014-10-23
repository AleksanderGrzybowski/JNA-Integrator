global asmfunc

section .text

; w [esp+4] wartość pierwszego argumentu



asmfunc:

	mov ecx, [esp+4+16]
	

	fld1
	fld1
	fsub ; 0 w st(0)

	; wartości krańcowe
	mov esi, [esp+8+16]
	fld qword [esi]
	fadd
	shl ecx, 3 ; bo double
	add esi, ecx
	shr ecx, 3
	fld qword [esi]
	fadd
	


	mov ecx, [esp+4+16]
	dec ecx
	mov esi, [esp+8+16]
	add esi, 8  ;pomijamy a[0]


petla:
	fadd qword [esi]
	fadd qword [esi]
	add esi, 8
	loop petla


	ret

