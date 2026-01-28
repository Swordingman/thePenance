/*     */ package com.esotericsoftware.spine38.utils;
/*     */ 
/*     */ import com.badlogic.gdx.utils.Array;
/*     */ import com.badlogic.gdx.utils.FloatArray;
/*     */ import com.badlogic.gdx.utils.ShortArray;
/*     */ import com.esotericsoftware.spine38.Slot;
/*     */ import com.esotericsoftware.spine38.attachments.ClippingAttachment;
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
/*     */ public class SkeletonClipping
/*     */ {
/*  39 */   private final Triangulator triangulator = new Triangulator();
/*  40 */   private final FloatArray clippingPolygon = new FloatArray();
/*  41 */   private final FloatArray clipOutput = new FloatArray(128);
/*  42 */   private final FloatArray clippedVertices = new FloatArray(128);
/*  43 */   private final ShortArray clippedTriangles = new ShortArray(128);
/*  44 */   private final FloatArray scratch = new FloatArray();
/*     */   
/*     */   private ClippingAttachment clipAttachment;
/*     */   private Array<FloatArray> clippingPolygons;
/*     */   
/*     */   public int clipStart(Slot slot, ClippingAttachment clip) {
/*  50 */     if (this.clipAttachment != null) return 0; 
/*  51 */     int n = clip.getWorldVerticesLength();
/*  52 */     if (n < 6) return 0; 
/*  53 */     this.clipAttachment = clip;
/*     */     
/*  55 */     float[] vertices = this.clippingPolygon.setSize(n);
/*  56 */     clip.computeWorldVertices(slot, 0, n, vertices, 0, 2);
/*  57 */     makeClockwise(this.clippingPolygon);
/*  58 */     ShortArray triangles = this.triangulator.triangulate(this.clippingPolygon);
/*  59 */     this.clippingPolygons = this.triangulator.decompose(this.clippingPolygon, triangles);
/*  60 */     for (FloatArray polygon : this.clippingPolygons) {
/*  61 */       makeClockwise(polygon);
/*  62 */       polygon.add(polygon.items[0]);
/*  63 */       polygon.add(polygon.items[1]);
/*     */     } 
/*  65 */     return this.clippingPolygons.size;
/*     */   }
/*     */   
/*     */   public void clipEnd(Slot slot) {
/*  69 */     if (this.clipAttachment != null && this.clipAttachment.getEndSlot() == slot.getData()) clipEnd(); 
/*     */   }
/*     */   
/*     */   public void clipEnd() {
/*  73 */     if (this.clipAttachment == null)
/*  74 */       return;  this.clipAttachment = null;
/*  75 */     this.clippingPolygons = null;
/*  76 */     this.clippedVertices.clear();
/*  77 */     this.clippedTriangles.clear();
/*  78 */     this.clippingPolygon.clear();
/*     */   }
/*     */   
/*     */   public boolean isClipping() {
/*  82 */     return (this.clipAttachment != null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void clipTriangles(float[] vertices, int verticesLength, short[] triangles, int trianglesLength, float[] uvs, float light, float dark, boolean twoColor) {
/*  88 */     FloatArray clipOutput = this.clipOutput, clippedVertices = this.clippedVertices;
/*  89 */     ShortArray clippedTriangles = this.clippedTriangles;
/*  90 */     Object[] polygons = this.clippingPolygons.items;
/*  91 */     int polygonsCount = this.clippingPolygons.size;
/*  92 */     int vertexSize = twoColor ? 6 : 5;
/*     */     
/*  94 */     short index = 0;
/*  95 */     clippedVertices.clear();
/*  96 */     clippedTriangles.clear();
/*     */     
/*  98 */     for (int i = 0; i < trianglesLength; i += 3) {
/*  99 */       int vertexOffset = triangles[i] << 1;
/* 100 */       float x1 = vertices[vertexOffset], y1 = vertices[vertexOffset + 1];
/* 101 */       float u1 = uvs[vertexOffset], v1 = uvs[vertexOffset + 1];
/*     */       
/* 103 */       vertexOffset = triangles[i + 1] << 1;
/* 104 */       float x2 = vertices[vertexOffset], y2 = vertices[vertexOffset + 1];
/* 105 */       float u2 = uvs[vertexOffset], v2 = uvs[vertexOffset + 1];
/*     */       
/* 107 */       vertexOffset = triangles[i + 2] << 1;
/* 108 */       float x3 = vertices[vertexOffset], y3 = vertices[vertexOffset + 1];
/* 109 */       float u3 = uvs[vertexOffset], v3 = uvs[vertexOffset + 1];
/*     */       
/* 111 */       for (int p = 0; p < polygonsCount; p++) {
/* 112 */         int s = clippedVertices.size;
/* 113 */         if (clip(x1, y1, x2, y2, x3, y3, (FloatArray)polygons[p], clipOutput)) {
/* 114 */           int clipOutputLength = clipOutput.size;
/* 115 */           if (clipOutputLength != 0) {
/* 116 */             float d0 = y2 - y3, d1 = x3 - x2, d2 = x1 - x3, d4 = y3 - y1;
/* 117 */             float d = 1.0F / (d0 * d2 + d1 * (y1 - y3));
/*     */             
/* 119 */             int clipOutputCount = clipOutputLength >> 1;
/* 120 */             float[] clipOutputItems = clipOutput.items;
/* 121 */             float[] clippedVerticesItems = clippedVertices.setSize(s + clipOutputCount * vertexSize);
/* 122 */             for (int ii = 0; ii < clipOutputLength; ii += 2) {
/* 123 */               float x = clipOutputItems[ii], y = clipOutputItems[ii + 1];
/* 124 */               clippedVerticesItems[s] = x;
/* 125 */               clippedVerticesItems[s + 1] = y;
/* 126 */               clippedVerticesItems[s + 2] = light;
/* 127 */               if (twoColor) {
/* 128 */                 clippedVerticesItems[s + 3] = dark;
/* 129 */                 s += 4;
/*     */               } else {
/* 131 */                 s += 3;
/* 132 */               }  float c0 = x - x3, c1 = y - y3;
/* 133 */               float a = (d0 * c0 + d1 * c1) * d;
/* 134 */               float b = (d4 * c0 + d2 * c1) * d;
/* 135 */               float c = 1.0F - a - b;
/* 136 */               clippedVerticesItems[s] = u1 * a + u2 * b + u3 * c;
/* 137 */               clippedVerticesItems[s + 1] = v1 * a + v2 * b + v3 * c;
/* 138 */               s += 2;
/*     */             } 
/*     */             
/* 141 */             s = clippedTriangles.size;
/* 142 */             short[] clippedTrianglesItems = clippedTriangles.setSize(s + 3 * (clipOutputCount - 2));
/* 143 */             clipOutputCount--;
/* 144 */             for (int j = 1; j < clipOutputCount; j++) {
/* 145 */               clippedTrianglesItems[s] = index;
/* 146 */               clippedTrianglesItems[s + 1] = (short)(index + j);
/* 147 */               clippedTrianglesItems[s + 2] = (short)(index + j + 1);
/* 148 */               s += 3;
/*     */             } 
/* 150 */             index = (short)(index + clipOutputCount + 1);
/*     */           } 
/*     */         } else {
/* 153 */           float[] clippedVerticesItems = clippedVertices.setSize(s + 3 * vertexSize);
/* 154 */           clippedVerticesItems[s] = x1;
/* 155 */           clippedVerticesItems[s + 1] = y1;
/* 156 */           clippedVerticesItems[s + 2] = light;
/* 157 */           if (!twoColor) {
/* 158 */             clippedVerticesItems[s + 3] = u1;
/* 159 */             clippedVerticesItems[s + 4] = v1;
/*     */             
/* 161 */             clippedVerticesItems[s + 5] = x2;
/* 162 */             clippedVerticesItems[s + 6] = y2;
/* 163 */             clippedVerticesItems[s + 7] = light;
/* 164 */             clippedVerticesItems[s + 8] = u2;
/* 165 */             clippedVerticesItems[s + 9] = v2;
/*     */             
/* 167 */             clippedVerticesItems[s + 10] = x3;
/* 168 */             clippedVerticesItems[s + 11] = y3;
/* 169 */             clippedVerticesItems[s + 12] = light;
/* 170 */             clippedVerticesItems[s + 13] = u3;
/* 171 */             clippedVerticesItems[s + 14] = v3;
/*     */           } else {
/* 173 */             clippedVerticesItems[s + 3] = dark;
/* 174 */             clippedVerticesItems[s + 4] = u1;
/* 175 */             clippedVerticesItems[s + 5] = v1;
/*     */             
/* 177 */             clippedVerticesItems[s + 6] = x2;
/* 178 */             clippedVerticesItems[s + 7] = y2;
/* 179 */             clippedVerticesItems[s + 8] = light;
/* 180 */             clippedVerticesItems[s + 9] = dark;
/* 181 */             clippedVerticesItems[s + 10] = u2;
/* 182 */             clippedVerticesItems[s + 11] = v2;
/*     */             
/* 184 */             clippedVerticesItems[s + 12] = x3;
/* 185 */             clippedVerticesItems[s + 13] = y3;
/* 186 */             clippedVerticesItems[s + 14] = light;
/* 187 */             clippedVerticesItems[s + 15] = dark;
/* 188 */             clippedVerticesItems[s + 16] = u3;
/* 189 */             clippedVerticesItems[s + 17] = v3;
/*     */           } 
/*     */           
/* 192 */           s = clippedTriangles.size;
/* 193 */           short[] clippedTrianglesItems = clippedTriangles.setSize(s + 3);
/* 194 */           clippedTrianglesItems[s] = index;
/* 195 */           clippedTrianglesItems[s + 1] = (short)(index + 1);
/* 196 */           clippedTrianglesItems[s + 2] = (short)(index + 2);
/* 197 */           index = (short)(index + 3);
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean clip(float x1, float y1, float x2, float y2, float x3, float y3, FloatArray clippingArea, FloatArray output) {
/* 207 */     FloatArray originalOutput = output;
/* 208 */     boolean clipped = false;
/*     */ 
/*     */     
/* 211 */     FloatArray input = null;
/* 212 */     if (clippingArea.size % 4 >= 2) {
/* 213 */       input = output;
/* 214 */       output = this.scratch;
/*     */     } else {
/* 216 */       input = this.scratch;
/*     */     } 
/* 218 */     input.clear();
/* 219 */     input.add(x1);
/* 220 */     input.add(y1);
/* 221 */     input.add(x2);
/* 222 */     input.add(y2);
/* 223 */     input.add(x3);
/* 224 */     input.add(y3);
/* 225 */     input.add(x1);
/* 226 */     input.add(y1);
/* 227 */     output.clear();
/*     */     
/* 229 */     float[] clippingVertices = clippingArea.items;
/* 230 */     int clippingVerticesLast = clippingArea.size - 4;
/* 231 */     for (int i = 0;; i += 2) {
/* 232 */       float edgeX = clippingVertices[i], edgeY = clippingVertices[i + 1];
/* 233 */       float edgeX2 = clippingVertices[i + 2], edgeY2 = clippingVertices[i + 3];
/* 234 */       float deltaX = edgeX - edgeX2, deltaY = edgeY - edgeY2;
/*     */       
/* 236 */       float[] inputVertices = input.items;
/* 237 */       int inputVerticesLength = input.size - 2, outputStart = output.size;
/* 238 */       for (int ii = 0; ii < inputVerticesLength; ii += 2) {
/* 239 */         float inputX = inputVertices[ii], inputY = inputVertices[ii + 1];
/* 240 */         float inputX2 = inputVertices[ii + 2], inputY2 = inputVertices[ii + 3];
/* 241 */         boolean side2 = (deltaX * (inputY2 - edgeY2) - deltaY * (inputX2 - edgeX2) > 0.0F);
/* 242 */         if (deltaX * (inputY - edgeY2) - deltaY * (inputX - edgeX2) > 0.0F)
/* 243 */         { if (side2)
/* 244 */           { output.add(inputX2);
/* 245 */             output.add(inputY2); }
/*     */           
/*     */           else
/*     */           
/* 249 */           { float c0 = inputY2 - inputY, c2 = inputX2 - inputX;
/* 250 */             float s = c0 * (edgeX2 - edgeX) - c2 * (edgeY2 - edgeY);
/* 251 */             if (Math.abs(s) > 1.0E-6F) {
/* 252 */               float ua = (c2 * (edgeY - inputY) - c0 * (edgeX - inputX)) / s;
/* 253 */               output.add(edgeX + (edgeX2 - edgeX) * ua);
/* 254 */               output.add(edgeY + (edgeY2 - edgeY) * ua);
/*     */             } else {
/* 256 */               output.add(edgeX);
/* 257 */               output.add(edgeY);
/*     */             } 
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
/* 273 */             clipped = true; }  } else { if (side2) { float c0 = inputY2 - inputY, c2 = inputX2 - inputX; float s = c0 * (edgeX2 - edgeX) - c2 * (edgeY2 - edgeY); if (Math.abs(s) > 1.0E-6F) { float ua = (c2 * (edgeY - inputY) - c0 * (edgeX - inputX)) / s; output.add(edgeX + (edgeX2 - edgeX) * ua); output.add(edgeY + (edgeY2 - edgeY) * ua); } else { output.add(edgeX); output.add(edgeY); }  output.add(inputX2); output.add(inputY2); }  clipped = true; }
/*     */       
/*     */       } 
/* 276 */       if (outputStart == output.size) {
/* 277 */         originalOutput.clear();
/* 278 */         return true;
/*     */       } 
/*     */       
/* 281 */       output.add(output.items[0]);
/* 282 */       output.add(output.items[1]);
/*     */       
/* 284 */       if (i == clippingVerticesLast)
/* 285 */         break;  FloatArray temp = output;
/* 286 */       output = input;
/* 287 */       output.clear();
/* 288 */       input = temp;
/*     */     } 
/*     */     
/* 291 */     if (originalOutput != output) {
/* 292 */       originalOutput.clear();
/* 293 */       originalOutput.addAll(output.items, 0, output.size - 2);
/*     */     } else {
/* 295 */       originalOutput.setSize(originalOutput.size - 2);
/*     */     } 
/* 297 */     return clipped;
/*     */   }
/*     */   
/*     */   public FloatArray getClippedVertices() {
/* 301 */     return this.clippedVertices;
/*     */   }
/*     */   
/*     */   public ShortArray getClippedTriangles() {
/* 305 */     return this.clippedTriangles;
/*     */   }
/*     */   
/*     */   static void makeClockwise(FloatArray polygon) {
/* 309 */     float[] vertices = polygon.items;
/* 310 */     int verticeslength = polygon.size;
/*     */     
/* 312 */     float area = vertices[verticeslength - 2] * vertices[1] - vertices[0] * vertices[verticeslength - 1]; int i, n;
/* 313 */     for (i = 0, n = verticeslength - 3; i < n; i += 2) {
/* 314 */       float p1x = vertices[i];
/* 315 */       float p1y = vertices[i + 1];
/* 316 */       float p2x = vertices[i + 2];
/* 317 */       float p2y = vertices[i + 3];
/* 318 */       area += p1x * p2y - p2x * p1y;
/*     */     } 
/* 320 */     if (area < 0.0F)
/*     */       return;  int lastX, j;
/* 322 */     for (i = 0, lastX = verticeslength - 2, j = verticeslength >> 1; i < j; i += 2) {
/* 323 */       float x = vertices[i], y = vertices[i + 1];
/* 324 */       int other = lastX - i;
/* 325 */       vertices[i] = vertices[other];
/* 326 */       vertices[i + 1] = vertices[other + 1];
/* 327 */       vertices[other] = x;
/* 328 */       vertices[other + 1] = y;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine3\\utils\SkeletonClipping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */