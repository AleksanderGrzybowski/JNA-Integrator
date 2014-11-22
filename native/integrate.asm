global integrateASM_FPU:function
global integrateASM_SSE:function
global testASMLibrary:function
global debugFunc:function
section .text

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

testASMLibrary: ; this is called at load time, to check if lib works
    mov eax, 1337
    ret

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

%define LEFT   esp+4
%define RIGHT  esp+12
%define POINTS esp+20
%define TAB    esp+24

const_TWO: dq 2.0

%define FPU_STACK_OFFSET 12 ; we push 3 registers on call

integrateASM_FPU:

    ; there used to be 'finit' right at the start
    ; but it works fine without it

    push eax ; we use these, we have to preserve them
    push ecx ; JVM may crash, not sure, but
    push esi ; better be careful

	mov ecx, [POINTS+FPU_STACK_OFFSET]

	fldz

	; add the first and the last value
	; in equation these are coeffs not mutiplied by 2
	mov esi, [TAB+FPU_STACK_OFFSET]
	fld qword [esi] ; first
	fadd
	shl ecx, 3 ; we need to find the last value
	add esi, ecx ; so we add offset (multiplied by 8 = sizeof(double))
	shr ecx, 3
	fld qword [esi] ; last
	fadd

	; prepare the loop

	mov ecx, [POINTS+FPU_STACK_OFFSET]
	dec ecx ; we will skip the last value, so one less iteration
	mov esi, [TAB+FPU_STACK_OFFSET]
	add esi, 8  ; we skip the first value, so start from the second (index of 1)


fpu_loop:
	fadd qword [esi]
	fadd qword [esi]
	add esi, 8
	loop fpu_loop

	; st(0) contains the sum a_0 + 2a_1 + 2a_2 + ... + 2a(n-1) + a_n
	; now we multiply all that sum by part on the left side of equation
	fld qword [RIGHT+FPU_STACK_OFFSET]
	fsub qword [LEFT+FPU_STACK_OFFSET]
	fdiv qword [const_TWO]
	fild dword [POINTS+FPU_STACK_OFFSET]
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
    movupd xmm3, [const_TWO]
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