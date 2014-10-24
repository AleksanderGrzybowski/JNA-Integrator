global integrateASM_FPU:function
global testASMLibrary:function
section .text

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

testASMLibrary:
    mov eax, 1337
    ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; [esp+4] = left
; [esp+12] = right
; [esp+20] = points
; [esp+24] = tab

dwa: dq 2.0

integrateASM_FPU:

    ;finit

    push eax
    push ecx
    push esi

    ;fld qword [dwa]
    ;ret

	mov ecx, [esp+20+12]


	fld1
	fld1
	fsub ; 0 w st(0)

	; wartości krańcowe
	mov esi, [esp+24+12]
	fld qword [esi]
	fadd
	shl ecx, 3 ; bo double
	add esi, ecx
	shr ecx, 3
	fld qword [esi] ; było +12
	fadd



	mov ecx, [esp+20+12]
	dec ecx
	mov esi, [esp+24+12]
	add esi, 8  ;pomijamy a[0] i a[n]


petla:
	fadd qword [esi]
	fadd qword [esi]
	add esi, 8
	loop petla

	; w st(0) jest wynik przed pomnożeniem

	fld qword [esp+12+12]
	fsub qword [esp+4+12]
	fdiv qword [dwa]
	fild dword [esp+20+12]
	fdiv
	fmul

    pop esi
    pop ecx
    pop eax

	ret
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


