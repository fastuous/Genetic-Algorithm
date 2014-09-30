kernel void simpleFitness(global const int* a, global const int* b, global int* c, int numElements)
{
  int iGID = get_global_id(0);
 
  if (iGID < numElements)
  {
    int aColor = a[iGID];
    int bColor = b[iGID];
  
    int dRed = (aColor >> 16) & 0xFF - (bColor >> 16) & 0xFF;
    int dGreen = (aColor >> 8) & 0xFF - (bColor >> 8) & 0xFF;
    int dBlue = (aColor & 0xFF) - (bColor & 0xFF);
  
  
    c[iGID] = dRed + dGreen + dBlue;
  }
}