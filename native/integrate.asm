global integrateASM_FPU:function
global integrateASM_SSE:function
global testASMLibrary:function
global debugFunc:function
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
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;




; [esp+4] = left
; [esp+12] = right
; [esp+20] = points
; [esp+24] = tab




integrateASM_SSE:

	push esi
    push ecx

    ;Push xmm0
    sub     esp, 16
    movdqu  [esp], xmm0
    ;Push xmm1
    sub     esp, 16
    movdqu  [esp], xmm1
    ;Push xmm2
    sub     esp, 16
    movdqu  [esp], xmm2
    ;Push xmm3
    sub     esp, 16
    movdqu  [esp], xmm3


    mov esi, [esp+24+8+64] ; w esi wskaźnik


    movq xmm0, [esi]
    add esi, [esp+20+8+64]
    add esi, [esp+20+8+64]
    add esi, [esp+20+8+64]
    add esi, [esp+20+8+64]
    add esi, [esp+20+8+64]
    add esi, [esp+20+8+64]
    add esi, [esp+20+8+64]
    add esi, [esp+20+8+64]
    movq xmm1, [esi]
    pslldq xmm0, 8
    addpd xmm0, xmm1


    mov esi, [esp+24+8+64]
    add esi, 8 ; skip

    mov ecx, [esp+20+8+64]
    sub ecx, 1
    shr ecx, 1
petlaSSE:
    movupd xmm3, [esi]
    addpd xmm0, xmm3
    addpd xmm0, xmm3
    add esi, 16
    loop petlaSSE


    ; dodanie
    movupd xmm1, xmm0
    pslldq xmm1, 8
    addpd xmm0, xmm1
    psrldq xmm0, 8
    ; xmm0[0-8] ma wynik

    ; końcówka

    movq xmm1, [esp+12+8+64]
    movupd xmm3, [esp+4+8+64]
    subsd xmm1, xmm3
    movupd xmm3, [dwa]
    divsd xmm1, xmm3
    ; konwersja
    movq xmm3, [esp+20+8+64]
    cvtdq2pd xmm2, xmm3
    ; podzielenie
    divsd xmm1, xmm2
    mulsd xmm0, xmm1

    push dword 0xaaaaaaaa
    push dword 0xaaaaaaaa
    ;;;;;;;
    movq [esp], xmm0
    fld qword [esp]
    ;;;;;;;;;;;;
    pop dword ecx
    pop dword ecx


    ;Pop xmm3
    movdqu  xmm3, [esp]
    add     esp, 16
    ;Pop xmm2
    movdqu  xmm2, [esp]
    add     esp, 16
    ;Pop xmm1
    movdqu  xmm1, [esp]
    add     esp, 16
    ;Pop xmm0
    movdqu  xmm0, [esp]
    add     esp, 16

    pop ecx
    pop esi
    ret

debugFunc:
	ret