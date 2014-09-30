/*
 * A kernel that takes an array of floats, copies them over into
 * another array and adds 1.0f.
 */
__kernel void add1(__global const float* a, __global float* c, int numElements)
{
  int iGID = get_global_id(0);
  if (iGID >= numElements) return;
  c[iGID] = a[iGID] + 1.0f;
}