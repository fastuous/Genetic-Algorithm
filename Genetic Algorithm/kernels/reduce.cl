/*
 * A kernel that takes an array of the [global ID * 2^step]th element
 * of an array and adds to it the [global ID * 2^(step-1)]th element.
 * For use in an O(log n) summation of pixel differences.
 * e.g. on step 1,
 *   differences[0] += differences[1]
 *   differences[2] += differences[3]
 *   ...
 * step 2,
 *   differences[0] += differences[2]
 *   differences[4] += differences[6]
 * ...
 * step 3,
 *   differences[0] += differences[4]
 *   differences[8] += differences[12]
 */
__kernel void reduceSum(__global long* differences, int step)
{
  int iGID = get_global_id(0);
  int multiplier = (int)pow(2.0, step);
  int index1 = multiplier * iGID;
  int index2 = index1 + multiplier / 2;
  differences[index1] += differences[index2];
}