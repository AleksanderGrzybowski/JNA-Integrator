global  integrateASM
global testASMLibrary:function


integrateASM:
        push    ebp
        mov     ebp,esp
        sub     esp,0x40        ; 64 bytes of local stack space
        mov     ebx,[ebp+8]     ; first parameter to function

        ; some more code

        leave                   ; mov esp,ebp / pop ebp
        ret



testASMLibrary:
    mov eax, 1337
    ret