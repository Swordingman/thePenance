/*     */ package com.esotericsoftware.spine38.attachments;
/*     */ 
/*     */ import com.badlogic.gdx.utils.FloatArray;
/*     */ import com.esotericsoftware.spine38.Bone;
/*     */ import com.esotericsoftware.spine38.Skeleton;
/*     */ import com.esotericsoftware.spine38.Slot;
/*     */ import com.esotericsoftware.spine38.utils.SpineUtils;
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
/*     */ 
/*     */ 
/*     */ public abstract class VertexAttachment
/*     */   extends Attachment
/*     */ {
/*     */   private static int nextID;
/*  45 */   private final int id = (nextID() & 0xFFFF) << 11;
/*     */   int[] bones;
/*     */   float[] vertices;
/*     */   int worldVerticesLength;
/*  49 */   VertexAttachment deformAttachment = this;
/*     */   
/*     */   public VertexAttachment(String name) {
/*  52 */     super(name);
/*     */   }
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
/*     */   public void computeWorldVertices(Slot slot, int start, int count, float[] worldVertices, int offset, int stride) {
/*  67 */     count = offset + (count >> 1) * stride;
/*  68 */     Skeleton skeleton = slot.getSkeleton();
/*  69 */     FloatArray deformArray = slot.getDeform();
/*  70 */     float[] vertices = this.vertices;
/*  71 */     int[] bones = this.bones;
/*  72 */     if (bones == null) {
/*  73 */       if (deformArray.size > 0) vertices = deformArray.items; 
/*  74 */       Bone bone = slot.getBone();
/*  75 */       float x = bone.getWorldX(), y = bone.getWorldY();
/*  76 */       float a = bone.getA(), b = bone.getB(), c = bone.getC(), d = bone.getD(); int w = 0;
/*  77 */       for (int j = start; w < count; j += 2, w += stride) {
/*  78 */         float vx = vertices[j], vy = vertices[j + 1];
/*  79 */         worldVertices[w] = vx * a + vy * b + x;
/*  80 */         worldVertices[w + 1] = vx * c + vy * d + y;
/*     */       } 
/*     */       return;
/*     */     } 
/*  84 */     int v = 0, skip = 0;
/*  85 */     for (int i = 0; i < start; i += 2) {
/*  86 */       int n = bones[v];
/*  87 */       v += n + 1;
/*  88 */       skip += n;
/*     */     } 
/*  90 */     Object[] skeletonBones = (skeleton.getBones()).items;
/*  91 */     if (deformArray.size == 0) {
/*  92 */       int w; int b; for (w = offset, b = skip * 3; w < count; w += stride) {
/*  93 */         float wx = 0.0F, wy = 0.0F;
/*  94 */         int n = bones[v++];
/*  95 */         n += v;
/*  96 */         for (; v < n; v++, b += 3) {
/*  97 */           Bone bone = (Bone)skeletonBones[bones[v]];
/*  98 */           float vx = vertices[b], vy = vertices[b + 1], weight = vertices[b + 2];
/*  99 */           wx += (vx * bone.getA() + vy * bone.getB() + bone.getWorldX()) * weight;
/* 100 */           wy += (vx * bone.getC() + vy * bone.getD() + bone.getWorldY()) * weight;
/*     */         } 
/* 102 */         worldVertices[w] = wx;
/* 103 */         worldVertices[w + 1] = wy;
/*     */       } 
/*     */     } else {
/* 106 */       float[] deform = deformArray.items; int w, b, f;
/* 107 */       for (w = offset, b = skip * 3, f = skip << 1; w < count; w += stride) {
/* 108 */         float wx = 0.0F, wy = 0.0F;
/* 109 */         int n = bones[v++];
/* 110 */         n += v;
/* 111 */         for (; v < n; v++, b += 3, f += 2) {
/* 112 */           Bone bone = (Bone)skeletonBones[bones[v]];
/* 113 */           float vx = vertices[b] + deform[f], vy = vertices[b + 1] + deform[f + 1], weight = vertices[b + 2];
/* 114 */           wx += (vx * bone.getA() + vy * bone.getB() + bone.getWorldX()) * weight;
/* 115 */           wy += (vx * bone.getC() + vy * bone.getD() + bone.getWorldY()) * weight;
/*     */         } 
/* 117 */         worldVertices[w] = wx;
/* 118 */         worldVertices[w + 1] = wy;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public VertexAttachment getDeformAttachment() {
/* 126 */     return this.deformAttachment;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDeformAttachment(VertexAttachment deformAttachment) {
/* 131 */     this.deformAttachment = deformAttachment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] getBones() {
/* 138 */     return this.bones;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBones(int[] bones) {
/* 143 */     this.bones = bones;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float[] getVertices() {
/* 150 */     return this.vertices;
/*     */   }
/*     */   
/*     */   public void setVertices(float[] vertices) {
/* 154 */     this.vertices = vertices;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWorldVerticesLength() {
/* 160 */     return this.worldVerticesLength;
/*     */   }
/*     */   
/*     */   public void setWorldVerticesLength(int worldVerticesLength) {
/* 164 */     this.worldVerticesLength = worldVerticesLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getId() {
/* 169 */     return this.id;
/*     */   }
/*     */ 
/*     */   
/*     */   void copyTo(VertexAttachment attachment) {
/* 174 */     if (this.bones != null) {
/* 175 */       attachment.bones = new int[this.bones.length];
/* 176 */       SpineUtils.arraycopy(this.bones, 0, attachment.bones, 0, this.bones.length);
/*     */     } else {
/* 178 */       attachment.bones = null;
/*     */     } 
/* 180 */     if (this.vertices != null) {
/* 181 */       attachment.vertices = new float[this.vertices.length];
/* 182 */       SpineUtils.arraycopy(this.vertices, 0, attachment.vertices, 0, this.vertices.length);
/*     */     } else {
/* 184 */       attachment.vertices = null;
/*     */     } 
/* 186 */     attachment.worldVerticesLength = this.worldVerticesLength;
/* 187 */     attachment.deformAttachment = this.deformAttachment;
/*     */   }
/*     */   
/*     */   private static synchronized int nextID() {
/* 191 */     return nextID++;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\attachments\VertexAttachment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */