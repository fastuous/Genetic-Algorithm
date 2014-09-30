/*
 * A kernel that takes an array of floats, copies them over into
 * another array and adds 1.0f.
 */
kernel void add1(global const float* a, global float* c)
{
  int iGID = get_global_id(0);
  c[iGID] = a[iGID] + 1.0f;
}