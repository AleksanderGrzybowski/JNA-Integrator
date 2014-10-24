import com.sun.jna.Library;
import com.sun.jna.Pointer;

/**
* Created by kelog on 24.10.14.
*/
interface NativeInterface extends Library {
	public double integrateC(double a, double b, int n, Pointer values);

	public double integrateASM_FPU(double a, double b, int n, Pointer values);

	public int testASMLibrary();
}
