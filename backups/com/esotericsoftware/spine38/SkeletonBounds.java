/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.utils.Array;
/*     */ import com.badlogic.gdx.utils.FloatArray;
/*     */ import com.badlogic.gdx.utils.Pool;
/*     */ import com.esotericsoftware.spine38.attachments.Attachment;
/*     */ import com.esotericsoftware.spine38.attachments.BoundingBoxAttachment;
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
/*     */ public class SkeletonBounds
/*     */ {
/*     */   private float minX;
/*     */   private float minY;
/*     */   private float maxX;
/*     */   private float maxY;
/*  42 */   private Array<BoundingBoxAttachment> boundingBoxes = new Array();
/*  43 */   private Array<FloatArray> polygons = new Array();
/*  44 */   private Pool<FloatArray> polygonPool = new Pool() {
/*     */       protected Object newObject() {
/*  46 */         return new FloatArray();
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void update(Skeleton skeleton, boolean updateAabb) {
/*  55 */     if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null."); 
/*  56 */     Array<BoundingBoxAttachment> boundingBoxes = this.boundingBoxes;
/*  57 */     Array<FloatArray> polygons = this.polygons;
/*  58 */     Array<Slot> slots = skeleton.slots;
/*  59 */     int slotCount = slots.size;
/*     */     
/*  61 */     boundingBoxes.clear();
/*  62 */     this.polygonPool.freeAll(polygons);
/*  63 */     polygons.clear();
/*     */     
/*  65 */     for (int i = 0; i < slotCount; i++) {
/*  66 */       Slot slot = (Slot)slots.get(i);
/*  67 */       if (slot.bone.active) {
/*  68 */         Attachment attachment = slot.attachment;
/*  69 */         if (attachment instanceof BoundingBoxAttachment) {
/*  70 */           BoundingBoxAttachment boundingBox = (BoundingBoxAttachment)attachment;
/*  71 */           boundingBoxes.add(boundingBox);
/*     */           
/*  73 */           FloatArray polygon = (FloatArray)this.polygonPool.obtain();
/*  74 */           polygons.add(polygon);
/*  75 */           boundingBox.computeWorldVertices(slot, 0, boundingBox.getWorldVerticesLength(), polygon
/*  76 */               .setSize(boundingBox.getWorldVerticesLength()), 0, 2);
/*     */         } 
/*     */       } 
/*     */     } 
/*  80 */     if (updateAabb) {
/*  81 */       aabbCompute();
/*     */     } else {
/*  83 */       this.minX = -2.14748365E9F;
/*  84 */       this.minY = -2.14748365E9F;
/*  85 */       this.maxX = 2.14748365E9F;
/*  86 */       this.maxY = 2.14748365E9F;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void aabbCompute() {
/*  91 */     float minX = 2.14748365E9F, minY = 2.14748365E9F, maxX = -2.14748365E9F, maxY = -2.14748365E9F;
/*  92 */     Array<FloatArray> polygons = this.polygons;
/*  93 */     for (int i = 0, n = polygons.size; i < n; i++) {
/*  94 */       FloatArray polygon = (FloatArray)polygons.get(i);
/*  95 */       float[] vertices = polygon.items;
/*  96 */       for (int ii = 0, nn = polygon.size; ii < nn; ii += 2) {
/*  97 */         float x = vertices[ii];
/*  98 */         float y = vertices[ii + 1];
/*  99 */         minX = Math.min(minX, x);
/* 100 */         minY = Math.min(minY, y);
/* 101 */         maxX = Math.max(maxX, x);
/* 102 */         maxY = Math.max(maxY, y);
/*     */       } 
/*     */     } 
/* 105 */     this.minX = minX;
/* 106 */     this.minY = minY;
/* 107 */     this.maxX = maxX;
/* 108 */     this.maxY = maxY;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean aabbContainsPoint(float x, float y) {
/* 113 */     return (x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean aabbIntersectsSegment(float x1, float y1, float x2, float y2) {
/* 118 */     float minX = this.minX;
/* 119 */     float minY = this.minY;
/* 120 */     float maxX = this.maxX;
/* 121 */     float maxY = this.maxY;
/* 122 */     if ((x1 <= minX && x2 <= minX) || (y1 <= minY && y2 <= minY) || (x1 >= maxX && x2 >= maxX) || (y1 >= maxY && y2 >= maxY))
/* 123 */       return false; 
/* 124 */     float m = (y2 - y1) / (x2 - x1);
/* 125 */     float y = m * (minX - x1) + y1;
/* 126 */     if (y > minY && y < maxY) return true; 
/* 127 */     y = m * (maxX - x1) + y1;
/* 128 */     if (y > minY && y < maxY) return true; 
/* 129 */     float x = (minY - y1) / m + x1;
/* 130 */     if (x > minX && x < maxX) return true; 
/* 131 */     x = (maxY - y1) / m + x1;
/* 132 */     if (x > minX && x < maxX) return true; 
/* 133 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean aabbIntersectsSkeleton(SkeletonBounds bounds) {
/* 138 */     if (bounds == null) throw new IllegalArgumentException("bounds cannot be null."); 
/* 139 */     return (this.minX < bounds.maxX && this.maxX > bounds.minX && this.minY < bounds.maxY && this.maxY > bounds.minY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BoundingBoxAttachment containsPoint(float x, float y) {
/* 145 */     Array<FloatArray> polygons = this.polygons;
/* 146 */     for (int i = 0, n = polygons.size; i < n; i++) {
/* 147 */       if (containsPoint((FloatArray)polygons.get(i), x, y)) return (BoundingBoxAttachment)this.boundingBoxes.get(i); 
/* 148 */     }  return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsPoint(FloatArray polygon, float x, float y) {
/* 153 */     if (polygon == null) throw new IllegalArgumentException("polygon cannot be null."); 
/* 154 */     float[] vertices = polygon.items;
/* 155 */     int nn = polygon.size;
/*     */     
/* 157 */     int prevIndex = nn - 2;
/* 158 */     boolean inside = false;
/* 159 */     for (int ii = 0; ii < nn; ii += 2) {
/* 160 */       float vertexY = vertices[ii + 1];
/* 161 */       float prevY = vertices[prevIndex + 1];
/* 162 */       if ((vertexY < y && prevY >= y) || (prevY < y && vertexY >= y)) {
/* 163 */         float vertexX = vertices[ii];
/* 164 */         if (vertexX + (y - vertexY) / (prevY - vertexY) * (vertices[prevIndex] - vertexX) < x) inside = !inside; 
/*     */       } 
/* 166 */       prevIndex = ii;
/*     */     } 
/* 168 */     return inside;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BoundingBoxAttachment intersectsSegment(float x1, float y1, float x2, float y2) {
/* 175 */     Array<FloatArray> polygons = this.polygons;
/* 176 */     for (int i = 0, n = polygons.size; i < n; i++) {
/* 177 */       if (intersectsSegment((FloatArray)polygons.get(i), x1, y1, x2, y2)) return (BoundingBoxAttachment)this.boundingBoxes.get(i); 
/* 178 */     }  return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean intersectsSegment(FloatArray polygon, float x1, float y1, float x2, float y2) {
/* 183 */     if (polygon == null) throw new IllegalArgumentException("polygon cannot be null."); 
/* 184 */     float[] vertices = polygon.items;
/* 185 */     int nn = polygon.size;
/*     */     
/* 187 */     float width12 = x1 - x2, height12 = y1 - y2;
/* 188 */     float det1 = x1 * y2 - y1 * x2;
/* 189 */     float x3 = vertices[nn - 2], y3 = vertices[nn - 1];
/* 190 */     for (int ii = 0; ii < nn; ii += 2) {
/* 191 */       float x4 = vertices[ii], y4 = vertices[ii + 1];
/* 192 */       float det2 = x3 * y4 - y3 * x4;
/* 193 */       float width34 = x3 - x4, height34 = y3 - y4;
/* 194 */       float det3 = width12 * height34 - height12 * width34;
/* 195 */       float x = (det1 * width34 - width12 * det2) / det3;
/* 196 */       if (((x >= x3 && x <= x4) || (x >= x4 && x <= x3)) && ((x >= x1 && x <= x2) || (x >= x2 && x <= x1))) {
/* 197 */         float y = (det1 * height34 - height12 * det2) / det3;
/* 198 */         if (((y >= y3 && y <= y4) || (y >= y4 && y <= y3)) && ((y >= y1 && y <= y2) || (y >= y2 && y <= y1))) return true; 
/*     */       } 
/* 200 */       x3 = x4;
/* 201 */       y3 = y4;
/*     */     } 
/* 203 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getMinX() {
/* 208 */     return this.minX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getMinY() {
/* 213 */     return this.minY;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getMaxX() {
/* 218 */     return this.maxX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getMaxY() {
/* 223 */     return this.maxY;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getWidth() {
/* 228 */     return this.maxX - this.minX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getHeight() {
/* 233 */     return this.maxY - this.minY;
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<BoundingBoxAttachment> getBoundingBoxes() {
/* 238 */     return this.boundingBoxes;
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<FloatArray> getPolygons() {
/* 243 */     return this.polygons;
/*     */   }
/*     */ 
/*     */   
/*     */   public FloatArray getPolygon(BoundingBoxAttachment boundingBox) {
/* 248 */     if (boundingBox == null) throw new IllegalArgumentException("boundingBox cannot be null."); 
/* 249 */     int index = this.boundingBoxes.indexOf(boundingBox, true);
/* 250 */     return (index == -1) ? null : (FloatArray)this.polygons.get(index);
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\SkeletonBounds.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */