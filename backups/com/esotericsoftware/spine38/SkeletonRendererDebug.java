/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.Gdx;
/*     */ import com.badlogic.gdx.graphics.Color;
/*     */ import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
/*     */ import com.badlogic.gdx.math.Vector2;
/*     */ import com.badlogic.gdx.utils.Array;
/*     */ import com.badlogic.gdx.utils.FloatArray;
/*     */ import com.esotericsoftware.spine38.attachments.Attachment;
/*     */ import com.esotericsoftware.spine38.attachments.BoundingBoxAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.ClippingAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.MeshAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.PathAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.PointAttachment;
/*     */ import com.esotericsoftware.spine38.attachments.RegionAttachment;
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
/*     */ 
/*     */ 
/*     */ public class SkeletonRendererDebug
/*     */ {
/*  49 */   private static final Color boneLineColor = Color.RED;
/*  50 */   private static final Color boneOriginColor = Color.GREEN;
/*  51 */   private static final Color attachmentLineColor = new Color(0.0F, 0.0F, 1.0F, 0.5F);
/*  52 */   private static final Color triangleLineColor = new Color(1.0F, 0.64F, 0.0F, 0.5F);
/*  53 */   private static final Color aabbColor = new Color(0.0F, 1.0F, 0.0F, 0.5F);
/*     */   
/*     */   private final ShapeRenderer shapes;
/*     */   private boolean drawBones = true, drawRegionAttachments = true, drawBoundingBoxes = true, drawPoints = true;
/*     */   private boolean drawMeshHull = true;
/*  58 */   private final SkeletonBounds bounds = new SkeletonBounds(); private boolean drawMeshTriangles = true; private boolean drawPaths = true; private boolean drawClipping = true;
/*  59 */   private final FloatArray vertices = new FloatArray(32);
/*  60 */   private float scale = 1.0F;
/*  61 */   private float boneWidth = 2.0F;
/*     */   private boolean premultipliedAlpha;
/*  63 */   private final Vector2 temp1 = new Vector2(), temp2 = new Vector2();
/*     */   
/*     */   public SkeletonRendererDebug() {
/*  66 */     this.shapes = new ShapeRenderer();
/*     */   }
/*     */   
/*     */   public SkeletonRendererDebug(ShapeRenderer shapes) {
/*  70 */     if (shapes == null) throw new IllegalArgumentException("shapes cannot be null."); 
/*  71 */     this.shapes = shapes;
/*     */   }
/*     */   
/*     */   public void draw(Skeleton skeleton) {
/*  75 */     if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null.");
/*     */     
/*  77 */     Gdx.gl.glEnable(3042);
/*  78 */     int srcFunc = this.premultipliedAlpha ? 1 : 770;
/*  79 */     Gdx.gl.glBlendFunc(srcFunc, 771);
/*     */     
/*  81 */     ShapeRenderer shapes = this.shapes;
/*  82 */     Array<Bone> bones = skeleton.getBones();
/*  83 */     Array<Slot> slots = skeleton.getSlots();
/*     */     
/*  85 */     shapes.begin(ShapeRenderer.ShapeType.Filled);
/*     */     
/*  87 */     if (this.drawBones) {
/*  88 */       for (int i = 0, n = bones.size; i < n; i++) {
/*  89 */         Bone bone = (Bone)bones.get(i);
/*  90 */         if (bone.parent != null && bone.active) {
/*  91 */           float length = bone.data.length, width = this.boneWidth;
/*  92 */           if (length == 0.0F) {
/*  93 */             length = 8.0F;
/*  94 */             width /= 2.0F;
/*  95 */             shapes.setColor(boneOriginColor);
/*     */           } else {
/*  97 */             shapes.setColor(boneLineColor);
/*  98 */           }  float x = length * bone.a + bone.worldX;
/*  99 */           float y = length * bone.c + bone.worldY;
/* 100 */           shapes.rectLine(bone.worldX, bone.worldY, x, y, width * this.scale);
/*     */         } 
/* 102 */       }  shapes.x(skeleton.getX(), skeleton.getY(), 4.0F * this.scale);
/*     */     } 
/*     */     
/* 105 */     if (this.drawPoints) {
/* 106 */       shapes.setColor(boneOriginColor);
/* 107 */       for (int i = 0, n = slots.size; i < n; i++) {
/* 108 */         Slot slot = (Slot)slots.get(i);
/* 109 */         Attachment attachment = slot.attachment;
/* 110 */         if (attachment instanceof PointAttachment) {
/* 111 */           PointAttachment point = (PointAttachment)attachment;
/* 112 */           point.computeWorldPosition(slot.getBone(), this.temp1);
/* 113 */           this.temp2.set(8.0F, 0.0F).rotate(point.computeWorldRotation(slot.getBone()));
/* 114 */           shapes.rectLine(this.temp1, this.temp2, this.boneWidth / 2.0F * this.scale);
/*     */         } 
/*     */       } 
/*     */     } 
/* 118 */     shapes.end();
/* 119 */     shapes.begin(ShapeRenderer.ShapeType.Line);
/*     */     
/* 121 */     if (this.drawRegionAttachments) {
/* 122 */       shapes.setColor(attachmentLineColor);
/* 123 */       for (int i = 0, n = slots.size; i < n; i++) {
/* 124 */         Slot slot = (Slot)slots.get(i);
/* 125 */         Attachment attachment = slot.attachment;
/* 126 */         if (attachment instanceof RegionAttachment) {
/* 127 */           RegionAttachment region = (RegionAttachment)attachment;
/* 128 */           float[] vertices = this.vertices.items;
/* 129 */           region.computeWorldVertices(slot.getBone(), vertices, 0, 2);
/* 130 */           shapes.line(vertices[0], vertices[1], vertices[2], vertices[3]);
/* 131 */           shapes.line(vertices[2], vertices[3], vertices[4], vertices[5]);
/* 132 */           shapes.line(vertices[4], vertices[5], vertices[6], vertices[7]);
/* 133 */           shapes.line(vertices[6], vertices[7], vertices[0], vertices[1]);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 138 */     if (this.drawMeshHull || this.drawMeshTriangles) {
/* 139 */       for (int i = 0, n = slots.size; i < n; i++) {
/* 140 */         Slot slot = (Slot)slots.get(i);
/* 141 */         Attachment attachment = slot.attachment;
/* 142 */         if (attachment instanceof MeshAttachment) {
/* 143 */           MeshAttachment mesh = (MeshAttachment)attachment;
/* 144 */           float[] vertices = this.vertices.setSize(mesh.getWorldVerticesLength());
/* 145 */           mesh.computeWorldVertices(slot, 0, mesh.getWorldVerticesLength(), vertices, 0, 2);
/* 146 */           short[] triangles = mesh.getTriangles();
/* 147 */           int hullLength = mesh.getHullLength();
/* 148 */           if (this.drawMeshTriangles) {
/* 149 */             shapes.setColor(triangleLineColor);
/* 150 */             for (int ii = 0, nn = triangles.length; ii < nn; ii += 3) {
/* 151 */               int v1 = triangles[ii] * 2, v2 = triangles[ii + 1] * 2, v3 = triangles[ii + 2] * 2;
/* 152 */               shapes.triangle(vertices[v1], vertices[v1 + 1], vertices[v2], vertices[v2 + 1], vertices[v3], vertices[v3 + 1]);
/*     */             } 
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 158 */           if (this.drawMeshHull && hullLength > 0) {
/* 159 */             shapes.setColor(attachmentLineColor);
/* 160 */             float lastX = vertices[hullLength - 2], lastY = vertices[hullLength - 1];
/* 161 */             for (int ii = 0, nn = hullLength; ii < nn; ii += 2) {
/* 162 */               float x = vertices[ii], y = vertices[ii + 1];
/* 163 */               shapes.line(x, y, lastX, lastY);
/* 164 */               lastX = x;
/* 165 */               lastY = y;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/* 171 */     if (this.drawBoundingBoxes) {
/* 172 */       SkeletonBounds bounds = this.bounds;
/* 173 */       bounds.update(skeleton, true);
/* 174 */       shapes.setColor(aabbColor);
/* 175 */       shapes.rect(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
/* 176 */       Array<FloatArray> polygons = bounds.getPolygons();
/* 177 */       Array<BoundingBoxAttachment> boxes = bounds.getBoundingBoxes();
/* 178 */       for (int i = 0, n = polygons.size; i < n; i++) {
/* 179 */         FloatArray polygon = (FloatArray)polygons.get(i);
/* 180 */         shapes.setColor(((BoundingBoxAttachment)boxes.get(i)).getColor());
/* 181 */         shapes.polygon(polygon.items, 0, polygon.size);
/*     */       } 
/*     */     } 
/*     */     
/* 185 */     if (this.drawClipping) {
/* 186 */       for (int i = 0, n = slots.size; i < n; i++) {
/* 187 */         Slot slot = (Slot)slots.get(i);
/* 188 */         Attachment attachment = slot.attachment;
/* 189 */         if (attachment instanceof ClippingAttachment) {
/* 190 */           ClippingAttachment clip = (ClippingAttachment)attachment;
/* 191 */           int nn = clip.getWorldVerticesLength();
/* 192 */           float[] vertices = this.vertices.setSize(nn);
/* 193 */           clip.computeWorldVertices(slot, 0, nn, vertices, 0, 2);
/* 194 */           shapes.setColor(clip.getColor());
/* 195 */           for (int ii = 2; ii < nn; ii += 2)
/* 196 */             shapes.line(vertices[ii - 2], vertices[ii - 1], vertices[ii], vertices[ii + 1]); 
/* 197 */           shapes.line(vertices[0], vertices[1], vertices[nn - 2], vertices[nn - 1]);
/*     */         } 
/*     */       } 
/*     */     }
/* 201 */     if (this.drawPaths) {
/* 202 */       for (int i = 0, n = slots.size; i < n; i++) {
/* 203 */         Slot slot = (Slot)slots.get(i);
/* 204 */         Attachment attachment = slot.attachment;
/* 205 */         if (attachment instanceof PathAttachment) {
/* 206 */           PathAttachment path = (PathAttachment)attachment;
/* 207 */           int nn = path.getWorldVerticesLength();
/* 208 */           float[] vertices = this.vertices.setSize(nn);
/* 209 */           path.computeWorldVertices(slot, 0, nn, vertices, 0, 2);
/* 210 */           Color color = path.getColor();
/* 211 */           float x1 = vertices[2], y1 = vertices[3], x2 = 0.0F, y2 = 0.0F;
/* 212 */           if (path.getClosed()) {
/* 213 */             shapes.setColor(color);
/* 214 */             float cx1 = vertices[0], cy1 = vertices[1], cx2 = vertices[nn - 2], cy2 = vertices[nn - 1];
/* 215 */             x2 = vertices[nn - 4];
/* 216 */             y2 = vertices[nn - 3];
/* 217 */             shapes.curve(x1, y1, cx1, cy1, cx2, cy2, x2, y2, 32);
/* 218 */             shapes.setColor(Color.LIGHT_GRAY);
/* 219 */             shapes.line(x1, y1, cx1, cy1);
/* 220 */             shapes.line(x2, y2, cx2, cy2);
/*     */           } 
/* 222 */           nn -= 4;
/* 223 */           for (int ii = 4; ii < nn; ii += 6) {
/* 224 */             float cx1 = vertices[ii], cy1 = vertices[ii + 1], cx2 = vertices[ii + 2], cy2 = vertices[ii + 3];
/* 225 */             x2 = vertices[ii + 4];
/* 226 */             y2 = vertices[ii + 5];
/* 227 */             shapes.setColor(color);
/* 228 */             shapes.curve(x1, y1, cx1, cy1, cx2, cy2, x2, y2, 32);
/* 229 */             shapes.setColor(Color.LIGHT_GRAY);
/* 230 */             shapes.line(x1, y1, cx1, cy1);
/* 231 */             shapes.line(x2, y2, cx2, cy2);
/* 232 */             x1 = x2;
/* 233 */             y1 = y2;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/* 238 */     shapes.end();
/* 239 */     shapes.begin(ShapeRenderer.ShapeType.Filled);
/*     */     
/* 241 */     if (this.drawBones) {
/* 242 */       shapes.setColor(boneOriginColor);
/* 243 */       for (int i = 0, n = bones.size; i < n; i++) {
/* 244 */         Bone bone = (Bone)bones.get(i);
/* 245 */         if (bone.active) {
/* 246 */           shapes.circle(bone.worldX, bone.worldY, 3.0F * this.scale, 8);
/*     */         }
/*     */       } 
/*     */     } 
/* 250 */     if (this.drawPoints) {
/* 251 */       shapes.setColor(boneOriginColor);
/* 252 */       for (int i = 0, n = slots.size; i < n; i++) {
/* 253 */         Slot slot = (Slot)slots.get(i);
/* 254 */         Attachment attachment = slot.attachment;
/* 255 */         if (attachment instanceof PointAttachment) {
/* 256 */           PointAttachment point = (PointAttachment)attachment;
/* 257 */           point.computeWorldPosition(slot.getBone(), this.temp1);
/* 258 */           shapes.circle(this.temp1.x, this.temp1.y, 3.0F * this.scale, 8);
/*     */         } 
/*     */       } 
/*     */     } 
/* 262 */     shapes.end();
/*     */   }
/*     */ 
/*     */   
/*     */   public ShapeRenderer getShapeRenderer() {
/* 267 */     return this.shapes;
/*     */   }
/*     */   
/*     */   public void setBones(boolean bones) {
/* 271 */     this.drawBones = bones;
/*     */   }
/*     */   
/*     */   public void setScale(float scale) {
/* 275 */     this.scale = scale;
/*     */   }
/*     */   
/*     */   public void setRegionAttachments(boolean regionAttachments) {
/* 279 */     this.drawRegionAttachments = regionAttachments;
/*     */   }
/*     */   
/*     */   public void setBoundingBoxes(boolean boundingBoxes) {
/* 283 */     this.drawBoundingBoxes = boundingBoxes;
/*     */   }
/*     */   
/*     */   public void setMeshHull(boolean meshHull) {
/* 287 */     this.drawMeshHull = meshHull;
/*     */   }
/*     */   
/*     */   public void setMeshTriangles(boolean meshTriangles) {
/* 291 */     this.drawMeshTriangles = meshTriangles;
/*     */   }
/*     */   
/*     */   public void setPaths(boolean paths) {
/* 295 */     this.drawPaths = paths;
/*     */   }
/*     */   
/*     */   public void setPoints(boolean points) {
/* 299 */     this.drawPoints = points;
/*     */   }
/*     */   
/*     */   public void setClipping(boolean clipping) {
/* 303 */     this.drawClipping = clipping;
/*     */   }
/*     */   
/*     */   public void setPremultipliedAlpha(boolean premultipliedAlpha) {
/* 307 */     this.premultipliedAlpha = premultipliedAlpha;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\SkeletonRendererDebug.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */