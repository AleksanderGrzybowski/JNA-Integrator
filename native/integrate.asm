; Declarations of global symbols that need to be exported

global integrateASM_FPU:function
global integrateASM_SSE:function
global testASMLibrary:function

section .text

; This function is defensively called on startup
; to make sure that library was imported and native calls work
testASMLibrary:
    mov eax, 1337
    ret



; Positions of function arguments passed through the stack, as
; well as number 2 as a constant
%define LEFT   esp+4
%define RIGHT  esp+12
%define POINTS esp+20
%define TAB    esp+24

const_TWO: dq 2.0


; We push 3 4-byte registers on fpu algorithm call
%define FPU_STACK_OFFSET 12

integrateASM_FPU:

    ; there used to be 'finit' right at the start
    ; but it works fine without it

    ; preserve used registers
    ; I'm not sure if it is needed
    ; but JVM was once crashing without it

    push eax
    push ecx
    push esi

    ; we need to have 0 in st(0)
    fldz

    ; we need to separately add the first and the last value
    ; in final equation these are coeffs not mutiplied by 2

    ; add the first value
    mov esi, [TAB+FPU_STACK_OFFSET]
    fld qword [esi]
    fadd

    ; we need to find and load the last value in memory
    ; so take into account the length parameter (in ecx)
    ; and the fact that sizeof(double) == 8, so we multiply
    ; the displacement by 8 (equiv. to << 3)
    mov ecx, [POINTS+FPU_STACK_OFFSET]
    shl ecx, 3
    add esi, ecx
    shr ecx, 3
    fld qword [esi]
    fadd

    ; prepare the loop to start
    mov ecx, [POINTS+FPU_STACK_OFFSET]
    dec ecx ; we will skip the last value, so one less iteration
    mov esi, [TAB+FPU_STACK_OFFSET]
    add esi, 8  ; we have already included the first value, so start from the second (index of 1)


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

    ; bring registers back
    pop esi
    pop ecx
    pop eax

    ret
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



; 4 xmm registers and 2 32-bit registers
%define SSE_STACK_OFFSET 64+8

integrateASM_SSE:

    ; Preserve used 32-bit registers
    push esi
    push ecx

    ; Preserve used xmm registers
    sub     esp, 16
    movdqu  [esp], xmm0
    sub     esp, 16
    movdqu  [esp], xmm1
    sub     esp, 16
    movdqu  [esp], xmm2
    sub     esp, 16
    movdqu  [esp], xmm3

    ; esi points to the first value
    mov esi, [TAB+SSE_STACK_OFFSET]

    ; add first and last value
    movq xmm0, [esi] ; first
    mov ecx, [POINTS+SSE_STACK_OFFSET]
    shl ecx, 3
    add esi, ecx
    movq xmm1, [esi] ; last
    pslldq xmm0, 8
    addpd xmm0, xmm1 ; add them

    ; prepare loop, skip first item and last (already in)
    mov esi, [TAB+SSE_STACK_OFFSET]
    add esi, 8 ; skip the first
    mov ecx, [POINTS+SSE_STACK_OFFSET]
    sub ecx, 1 ; skip the last
    shr ecx, 1 ; we add two doubles at the same time, so 2x less iterations

sse_loop:
    movupd xmm3, [esi]
    addpd xmm0, xmm3
    addpd xmm0, xmm3 ; twice, cause there is 2*element in equation
    add esi, 16 ; two doubles
    loop sse_loop


    ; sum of coefficients
    movupd xmm1, xmm0
    pslldq xmm1, 8
    addpd xmm0, xmm1
    psrldq xmm0, 8
    ; overall sum a_0...a_n is in xmm0[0-8]

    ; multiply by coefficient on the left side of equations

    movq xmm1, [RIGHT+SSE_STACK_OFFSET]
    movupd xmm3, [LEFT+SSE_STACK_OFFSET]
    subsd xmm1, xmm3
    movupd xmm3, [const_TWO]
    divsd xmm1, xmm3
    ; conversion
    movq xmm3, [POINTS+SSE_STACK_OFFSET]
    cvtdq2pd xmm2, xmm3
    ; division
    divsd xmm1, xmm2
    mulsd xmm0, xmm1


    ; make room on stack, we need to move from xmm0 to st(0)
    ; in order to pass it back to the caller (by convention)
    push dword 0xaaaaaaaa
    push dword 0xaaaaaaaa

    movq [esp], xmm0
    fld qword [esp]

    pop dword ecx
    pop dword ecx


    ; pop everything back
    movdqu  xmm3, [esp]
    add     esp, 16
    movdqu  xmm2, [esp]
    add     esp, 16
    movdqu  xmm1, [esp]
    add     esp, 16
    movdqu  xmm0, [esp]
    add     esp, 16

    pop ecx
    pop esi
    ret
