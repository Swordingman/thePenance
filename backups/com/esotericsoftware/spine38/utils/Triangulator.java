/*     */ package com.esotericsoftware.spine38.utils;
/*     */ 
/*     */ import com.badlogic.gdx.utils.Array;
/*     */ import com.badlogic.gdx.utils.BooleanArray;
/*     */ import com.badlogic.gdx.utils.FloatArray;
/*     */ import com.badlogic.gdx.utils.Pool;
/*     */ import com.badlogic.gdx.utils.ShortArray;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class Triangulator
/*     */ {
/*  39 */   private final Array<FloatArray> convexPolygons = new Array();
/*  40 */   private final Array<ShortArray> convexPolygonsIndices = new Array();
/*     */   
/*  42 */   private final ShortArray indicesArray = new ShortArray();
/*  43 */   private final BooleanArray isConcaveArray = new BooleanArray();
/*  44 */   private final ShortArray triangles = new ShortArray();
/*     */   
/*  46 */   private final Pool<FloatArray> polygonPool = new Pool() {
/*     */       protected FloatArray newObject() {
/*  48 */         return new FloatArray(16);
/*     */       }
/*     */     };
/*     */   
/*  52 */   private final Pool<ShortArray> polygonIndicesPool = new Pool() {
/*     */       protected ShortArray newObject() {
/*  54 */         return new ShortArray(16);
/*     */       }
/*     */     };
/*     */   
/*     */   public ShortArray triangulate(FloatArray verticesArray) {
/*  59 */     float[] vertices = verticesArray.items;
/*  60 */     int vertexCount = verticesArray.size >> 1;
/*     */     
/*  62 */     ShortArray indicesArray = this.indicesArray;
/*  63 */     indicesArray.clear();
/*  64 */     short[] indices = indicesArray.setSize(vertexCount); short i;
/*  65 */     for (i = 0; i < vertexCount; i = (short)(i + 1)) {
/*  66 */       indices[i] = i;
/*     */     }
/*  68 */     BooleanArray isConcaveArray = this.isConcaveArray;
/*  69 */     boolean[] isConcave = isConcaveArray.setSize(vertexCount);
/*  70 */     for (int j = 0, n = vertexCount; j < n; j++) {
/*  71 */       isConcave[j] = isConcave(j, vertexCount, vertices, indices);
/*     */     }
/*  73 */     ShortArray triangles = this.triangles;
/*  74 */     triangles.clear();
/*  75 */     triangles.ensureCapacity(Math.max(0, vertexCount - 2) << 2);
/*     */     
/*  77 */     while (vertexCount > 3) {
/*     */       
/*  79 */       int previous = vertexCount - 1, k = 0, next = 1;
/*     */       
/*     */       label48: while (true) {
/*  82 */         if (!isConcave[k]) {
/*  83 */           int p1 = indices[previous] << 1, p2 = indices[k] << 1, p3 = indices[next] << 1;
/*  84 */           float p1x = vertices[p1], p1y = vertices[p1 + 1];
/*  85 */           float p2x = vertices[p2], p2y = vertices[p2 + 1];
/*  86 */           float p3x = vertices[p3], p3y = vertices[p3 + 1]; int ii;
/*  87 */           for (ii = (next + 1) % vertexCount; ii != previous; ii = (ii + 1) % vertexCount) {
/*  88 */             if (isConcave[ii]) {
/*  89 */               int v = indices[ii] << 1;
/*  90 */               float vx = vertices[v], vy = vertices[v + 1];
/*  91 */               if (positiveArea(p3x, p3y, p1x, p1y, vx, vy) && 
/*  92 */                 positiveArea(p1x, p1y, p2x, p2y, vx, vy) && 
/*  93 */                 positiveArea(p2x, p2y, p3x, p3y, vx, vy)) {
/*     */                 continue label48;
/*     */               }
/*     */             } 
/*     */           } 
/*     */           break;
/*     */         } 
/* 100 */         if (next == 0) {
/*     */           
/*     */           do {
/* 103 */             k--;
/* 104 */           } while (isConcave[k] && k > 0);
/*     */           
/*     */           break;
/*     */         } 
/* 108 */         previous = k;
/* 109 */         k = next;
/* 110 */         next = (next + 1) % vertexCount;
/*     */       } 
/*     */ 
/*     */       
/* 114 */       triangles.add(indices[(vertexCount + k - 1) % vertexCount]);
/* 115 */       triangles.add(indices[k]);
/* 116 */       triangles.add(indices[(k + 1) % vertexCount]);
/* 117 */       indicesArray.removeIndex(k);
/* 118 */       isConcaveArray.removeIndex(k);
/* 119 */       vertexCount--;
/*     */       
/* 121 */       int previousIndex = (vertexCount + k - 1) % vertexCount;
/* 122 */       int nextIndex = (k == vertexCount) ? 0 : k;
/* 123 */       isConcave[previousIndex] = isConcave(previousIndex, vertexCount, vertices, indices);
/* 124 */       isConcave[nextIndex] = isConcave(nextIndex, vertexCount, vertices, indices);
/*     */     } 
/*     */     
/* 127 */     if (vertexCount == 3) {
/* 128 */       triangles.add(indices[2]);
/* 129 */       triangles.add(indices[0]);
/* 130 */       triangles.add(indices[1]);
/*     */     } 
/*     */     
/* 133 */     return triangles;
/*     */   }
/*     */   
/*     */   public Array<FloatArray> decompose(FloatArray verticesArray, ShortArray triangles) {
/* 137 */     float[] vertices = verticesArray.items;
/*     */     
/* 139 */     Array<FloatArray> convexPolygons = this.convexPolygons;
/* 140 */     this.polygonPool.freeAll(convexPolygons);
/* 141 */     convexPolygons.clear();
/*     */     
/* 143 */     Array<ShortArray> convexPolygonsIndices = this.convexPolygonsIndices;
/* 144 */     this.polygonIndicesPool.freeAll(convexPolygonsIndices);
/* 145 */     convexPolygonsIndices.clear();
/*     */     
/* 147 */     ShortArray polygonIndices = (ShortArray)this.polygonIndicesPool.obtain();
/* 148 */     polygonIndices.clear();
/*     */     
/* 150 */     FloatArray polygon = (FloatArray)this.polygonPool.obtain();
/* 151 */     polygon.clear();
/*     */ 
/*     */     
/* 154 */     int fanBaseIndex = -1, lastWinding = 0;
/* 155 */     short[] trianglesItems = triangles.items; int i, n;
/* 156 */     for (i = 0, n = triangles.size; i < n; i += 3) {
/* 157 */       int t1 = trianglesItems[i] << 1, t2 = trianglesItems[i + 1] << 1, t3 = trianglesItems[i + 2] << 1;
/* 158 */       float x1 = vertices[t1], y1 = vertices[t1 + 1];
/* 159 */       float x2 = vertices[t2], y2 = vertices[t2 + 1];
/* 160 */       float x3 = vertices[t3], y3 = vertices[t3 + 1];
/*     */ 
/*     */       
/* 163 */       boolean merged = false;
/* 164 */       if (fanBaseIndex == t1) {
/* 165 */         int o = polygon.size - 4;
/* 166 */         float[] p = polygon.items;
/* 167 */         int winding1 = winding(p[o], p[o + 1], p[o + 2], p[o + 3], x3, y3);
/* 168 */         int winding2 = winding(x3, y3, p[0], p[1], p[2], p[3]);
/* 169 */         if (winding1 == lastWinding && winding2 == lastWinding) {
/* 170 */           polygon.add(x3);
/* 171 */           polygon.add(y3);
/* 172 */           polygonIndices.add(t3);
/* 173 */           merged = true;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 178 */       if (!merged) {
/* 179 */         if (polygon.size > 0) {
/* 180 */           convexPolygons.add(polygon);
/* 181 */           convexPolygonsIndices.add(polygonIndices);
/*     */         } else {
/* 183 */           this.polygonPool.free(polygon);
/* 184 */           this.polygonIndicesPool.free(polygonIndices);
/*     */         } 
/* 186 */         polygon = (FloatArray)this.polygonPool.obtain();
/* 187 */         polygon.clear();
/* 188 */         polygon.add(x1);
/* 189 */         polygon.add(y1);
/* 190 */         polygon.add(x2);
/* 191 */         polygon.add(y2);
/* 192 */         polygon.add(x3);
/* 193 */         polygon.add(y3);
/* 194 */         polygonIndices = (ShortArray)this.polygonIndicesPool.obtain();
/* 195 */         polygonIndices.clear();
/* 196 */         polygonIndices.add(t1);
/* 197 */         polygonIndices.add(t2);
/* 198 */         polygonIndices.add(t3);
/* 199 */         lastWinding = winding(x1, y1, x2, y2, x3, y3);
/* 200 */         fanBaseIndex = t1;
/*     */       } 
/*     */     } 
/*     */     
/* 204 */     if (polygon.size > 0) {
/* 205 */       convexPolygons.add(polygon);
/* 206 */       convexPolygonsIndices.add(polygonIndices);
/*     */     } 
/*     */ 
/*     */     
/* 210 */     for (i = 0, n = convexPolygons.size; i < n; i++) {
/* 211 */       polygonIndices = (ShortArray)convexPolygonsIndices.get(i);
/* 212 */       if (polygonIndices.size != 0) {
/* 213 */         int firstIndex = polygonIndices.get(0);
/* 214 */         int lastIndex = polygonIndices.get(polygonIndices.size - 1);
/*     */         
/* 216 */         polygon = (FloatArray)convexPolygons.get(i);
/* 217 */         int o = polygon.size - 4;
/* 218 */         float[] p = polygon.items;
/* 219 */         float prevPrevX = p[o], prevPrevY = p[o + 1];
/* 220 */         float prevX = p[o + 2], prevY = p[o + 3];
/* 221 */         float firstX = p[0], firstY = p[1];
/* 222 */         float secondX = p[2], secondY = p[3];
/* 223 */         int winding = winding(prevPrevX, prevPrevY, prevX, prevY, firstX, firstY);
/*     */         
/* 225 */         for (int ii = 0; ii < n; ii++) {
/* 226 */           if (ii != i) {
/* 227 */             ShortArray otherIndices = (ShortArray)convexPolygonsIndices.get(ii);
/* 228 */             if (otherIndices.size == 3) {
/* 229 */               int otherFirstIndex = otherIndices.get(0);
/* 230 */               int otherSecondIndex = otherIndices.get(1);
/* 231 */               int otherLastIndex = otherIndices.get(2);
/*     */               
/* 233 */               FloatArray otherPoly = (FloatArray)convexPolygons.get(ii);
/* 234 */               float x3 = otherPoly.get(otherPoly.size - 2), y3 = otherPoly.get(otherPoly.size - 1);
/*     */               
/* 236 */               if (otherFirstIndex == firstIndex && otherSecondIndex == lastIndex) {
/* 237 */                 int winding1 = winding(prevPrevX, prevPrevY, prevX, prevY, x3, y3);
/* 238 */                 int winding2 = winding(x3, y3, firstX, firstY, secondX, secondY);
/* 239 */                 if (winding1 == winding && winding2 == winding)
/* 240 */                 { otherPoly.clear();
/* 241 */                   otherIndices.clear();
/* 242 */                   polygon.add(x3);
/* 243 */                   polygon.add(y3);
/* 244 */                   polygonIndices.add(otherLastIndex);
/* 245 */                   prevPrevX = prevX;
/* 246 */                   prevPrevY = prevY;
/* 247 */                   prevX = x3;
/* 248 */                   prevY = y3;
/* 249 */                   ii = 0; } 
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 255 */     }  for (i = convexPolygons.size - 1; i >= 0; i--) {
/* 256 */       polygon = (FloatArray)convexPolygons.get(i);
/* 257 */       if (polygon.size == 0) {
/* 258 */         convexPolygons.removeIndex(i);
/* 259 */         this.polygonPool.free(polygon);
/* 260 */         polygonIndices = (ShortArray)convexPolygonsIndices.removeIndex(i);
/* 261 */         this.polygonIndicesPool.free(polygonIndices);
/*     */       } 
/*     */     } 
/*     */     
/* 265 */     return convexPolygons;
/*     */   }
/*     */   
/*     */   private static boolean isConcave(int index, int vertexCount, float[] vertices, short[] indices) {
/* 269 */     int previous = indices[(vertexCount + index - 1) % vertexCount] << 1;
/* 270 */     int current = indices[index] << 1;
/* 271 */     int next = indices[(index + 1) % vertexCount] << 1;
/* 272 */     return !positiveArea(vertices[previous], vertices[previous + 1], vertices[current], vertices[current + 1], vertices[next], vertices[next + 1]);
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean positiveArea(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
/* 277 */     return (p1x * (p3y - p2y) + p2x * (p1y - p3y) + p3x * (p2y - p1y) >= 0.0F);
/*     */   }
/*     */   
/*     */   private static int winding(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
/* 281 */     float px = p2x - p1x, py = p2y - p1y;
/* 282 */     return (p3x * py - p3y * px + px * p1y - p1x * py >= 0.0F) ? 1 : -1;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine3\\utils\Triangulator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */