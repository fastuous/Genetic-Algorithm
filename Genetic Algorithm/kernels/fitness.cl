__kernel void fitness(
    __global const int * reference,
    __global const int * triangles,
    __global int * differences,
    int elementCount
    )
{
  int iGID = get_global_id(0);
  
  if (iGID > elementCount) return;
  
  int rRGB = reference[iGID];
  int tRGB = triangles[iGID];
  float rr = (float) ( (rRGB >> 0x04) & 0xFF );
  float rg = (float) ( (rRGB >> 0x02) & 0xFF );
  float rb = (float) ( rRGB & 0xFF );
  float tr = (float) ( (tRGB >> 0x04) & 0xFF );
  float tg = (float) ( (tRGB >> 0x02) & 0xFF );
  float tb = (float) ( tRGB & 0xFF );
  
  float dr2 = pow(rr - tr, 2.0f);
  float dg2 = pow(rg - tg, 2.0f);
  float db2 = pow(rb - tb, 2.0f);
  
  int d = (int)sqrt(dr2 + dg2 + db2);
  
  differences[iGID] = d;
}