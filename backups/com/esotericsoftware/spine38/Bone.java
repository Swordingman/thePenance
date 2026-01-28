/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.math.Matrix3;
/*     */ import com.badlogic.gdx.math.Vector2;
/*     */ import com.badlogic.gdx.utils.Array;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Bone
/*     */   implements Updatable
/*     */ {
/*     */   final BoneData data;
/*     */   final Skeleton skeleton;
/*     */   final Bone parent;
/*  50 */   final Array<Bone> children = new Array(); float x; float y; float rotation;
/*     */   float scaleX;
/*     */   float scaleY;
/*     */   float shearX;
/*     */   float shearY;
/*     */   float ax;
/*     */   float ay;
/*     */   float arotation;
/*     */   float ascaleX;
/*     */   
/*     */   public Bone(BoneData data, Skeleton skeleton, Bone parent) {
/*  61 */     if (data == null) throw new IllegalArgumentException("data cannot be null."); 
/*  62 */     if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null."); 
/*  63 */     this.data = data;
/*  64 */     this.skeleton = skeleton;
/*  65 */     this.parent = parent;
/*  66 */     setToSetupPose();
/*     */   }
/*     */   float ascaleY; float ashearX; float ashearY; boolean appliedValid; float a;
/*     */   float b;
/*     */   
/*     */   public Bone(Bone bone, Skeleton skeleton, Bone parent) {
/*  72 */     if (bone == null) throw new IllegalArgumentException("bone cannot be null."); 
/*  73 */     if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null."); 
/*  74 */     this.skeleton = skeleton;
/*  75 */     this.parent = parent;
/*  76 */     this.data = bone.data;
/*  77 */     this.x = bone.x;
/*  78 */     this.y = bone.y;
/*  79 */     this.rotation = bone.rotation;
/*  80 */     this.scaleX = bone.scaleX;
/*  81 */     this.scaleY = bone.scaleY;
/*  82 */     this.shearX = bone.shearX;
/*  83 */     this.shearY = bone.shearY;
/*     */   }
/*     */   float worldX; float c; float d; float worldY; boolean sorted; boolean active;
/*     */   
/*     */   public void update() {
/*  88 */     updateWorldTransform(this.x, this.y, this.rotation, this.scaleX, this.scaleY, this.shearX, this.shearY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateWorldTransform() {
/*  95 */     updateWorldTransform(this.x, this.y, this.rotation, this.scaleX, this.scaleY, this.shearX, this.shearY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateWorldTransform(float x, float y, float rotation, float scaleX, float scaleY, float shearX, float shearY) {
/*     */     float rotationY, s, cos, la, prx, sin, lb, rx, za, lc, ry, zc, ld, f2, f1, f3, r, f4, zb, f5, zd, f6, f7, f8, f9;
/* 103 */     this.ax = x;
/* 104 */     this.ay = y;
/* 105 */     this.arotation = rotation;
/* 106 */     this.ascaleX = scaleX;
/* 107 */     this.ascaleY = scaleY;
/* 108 */     this.ashearX = shearX;
/* 109 */     this.ashearY = shearY;
/* 110 */     this.appliedValid = true;
/*     */     
/* 112 */     Bone parent = this.parent;
/* 113 */     if (parent == null) {
/* 114 */       Skeleton skeleton = this.skeleton;
/* 115 */       float f10 = rotation + 90.0F + shearY, sx = skeleton.scaleX, sy = skeleton.scaleY;
/* 116 */       this.a = SpineUtils.cosDeg(rotation + shearX) * scaleX * sx;
/* 117 */       this.b = SpineUtils.cosDeg(f10) * scaleY * sx;
/* 118 */       this.c = SpineUtils.sinDeg(rotation + shearX) * scaleX * sy;
/* 119 */       this.d = SpineUtils.sinDeg(f10) * scaleY * sy;
/* 120 */       this.worldX = x * sx + skeleton.x;
/* 121 */       this.worldY = y * sy + skeleton.y;
/*     */       
/*     */       return;
/*     */     } 
/* 125 */     float pa = parent.a, pb = parent.b, pc = parent.c, pd = parent.d;
/* 126 */     this.worldX = pa * x + pb * y + parent.worldX;
/* 127 */     this.worldY = pc * x + pd * y + parent.worldY;
/*     */     
/* 129 */     switch (this.data.transformMode) {
/*     */       case normal:
/* 131 */         rotationY = rotation + 90.0F + shearY;
/* 132 */         la = SpineUtils.cosDeg(rotation + shearX) * scaleX;
/* 133 */         lb = SpineUtils.cosDeg(rotationY) * scaleY;
/* 134 */         lc = SpineUtils.sinDeg(rotation + shearX) * scaleX;
/* 135 */         ld = SpineUtils.sinDeg(rotationY) * scaleY;
/* 136 */         this.a = pa * la + pb * lc;
/* 137 */         this.b = pa * lb + pb * ld;
/* 138 */         this.c = pc * la + pd * lc;
/* 139 */         this.d = pc * lb + pd * ld;
/*     */         return;
/*     */       
/*     */       case onlyTranslation:
/* 143 */         rotationY = rotation + 90.0F + shearY;
/* 144 */         this.a = SpineUtils.cosDeg(rotation + shearX) * scaleX;
/* 145 */         this.b = SpineUtils.cosDeg(rotationY) * scaleY;
/* 146 */         this.c = SpineUtils.sinDeg(rotation + shearX) * scaleX;
/* 147 */         this.d = SpineUtils.sinDeg(rotationY) * scaleY;
/*     */         break;
/*     */       
/*     */       case noRotationOrReflection:
/* 151 */         s = pa * pa + pc * pc;
/* 152 */         if (s > 1.0E-4F) {
/* 153 */           s = Math.abs(pa * pd - pb * pc) / s;
/* 154 */           pb = pc * s;
/* 155 */           pd = pa * s;
/* 156 */           prx = SpineUtils.atan2(pc, pa) * 57.295776F;
/*     */         } else {
/* 158 */           pa = 0.0F;
/* 159 */           pc = 0.0F;
/* 160 */           prx = 90.0F - SpineUtils.atan2(pd, pb) * 57.295776F;
/*     */         } 
/* 162 */         rx = rotation + shearX - prx;
/* 163 */         ry = rotation + shearY - prx + 90.0F;
/* 164 */         f2 = SpineUtils.cosDeg(rx) * scaleX;
/* 165 */         f3 = SpineUtils.cosDeg(ry) * scaleY;
/* 166 */         f4 = SpineUtils.sinDeg(rx) * scaleX;
/* 167 */         f5 = SpineUtils.sinDeg(ry) * scaleY;
/* 168 */         this.a = pa * f2 - pb * f4;
/* 169 */         this.b = pa * f3 - pb * f5;
/* 170 */         this.c = pc * f2 + pd * f4;
/* 171 */         this.d = pc * f3 + pd * f5;
/*     */         return;
/*     */       
/*     */       case noScale:
/*     */       case noScaleOrReflection:
/* 176 */         cos = SpineUtils.cosDeg(rotation); sin = SpineUtils.sinDeg(rotation);
/* 177 */         za = (pa * cos + pb * sin) / this.skeleton.scaleX;
/* 178 */         zc = (pc * cos + pd * sin) / this.skeleton.scaleY;
/* 179 */         f1 = (float)Math.sqrt((za * za + zc * zc));
/* 180 */         if (f1 > 1.0E-5F) f1 = 1.0F / f1; 
/* 181 */         za *= f1;
/* 182 */         zc *= f1;
/* 183 */         f1 = (float)Math.sqrt((za * za + zc * zc));
/* 184 */         if (this.data.transformMode == BoneData.TransformMode.noScale) if (((pa * pd - pb * pc < 0.0F) ? true : false) != ((((this.skeleton.scaleX < 0.0F) ? true : false) != ((this.skeleton.scaleY < 0.0F) ? true : false)) ? true : false))
/* 185 */             f1 = -f1;  
/* 186 */         r = 1.5707964F + SpineUtils.atan2(zc, za);
/* 187 */         zb = SpineUtils.cos(r) * f1;
/* 188 */         zd = SpineUtils.sin(r) * f1;
/* 189 */         f6 = SpineUtils.cosDeg(shearX) * scaleX;
/* 190 */         f7 = SpineUtils.cosDeg(90.0F + shearY) * scaleY;
/* 191 */         f8 = SpineUtils.sinDeg(shearX) * scaleX;
/* 192 */         f9 = SpineUtils.sinDeg(90.0F + shearY) * scaleY;
/* 193 */         this.a = za * f6 + zb * f8;
/* 194 */         this.b = za * f7 + zb * f9;
/* 195 */         this.c = zc * f6 + zd * f8;
/* 196 */         this.d = zc * f7 + zd * f9;
/*     */         break;
/*     */     } 
/*     */     
/* 200 */     this.a *= this.skeleton.scaleX;
/* 201 */     this.b *= this.skeleton.scaleX;
/* 202 */     this.c *= this.skeleton.scaleY;
/* 203 */     this.d *= this.skeleton.scaleY;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setToSetupPose() {
/* 208 */     BoneData data = this.data;
/* 209 */     this.x = data.x;
/* 210 */     this.y = data.y;
/* 211 */     this.rotation = data.rotation;
/* 212 */     this.scaleX = data.scaleX;
/* 213 */     this.scaleY = data.scaleY;
/* 214 */     this.shearX = data.shearX;
/* 215 */     this.shearY = data.shearY;
/*     */   }
/*     */ 
/*     */   
/*     */   public BoneData getData() {
/* 220 */     return this.data;
/*     */   }
/*     */ 
/*     */   
/*     */   public Skeleton getSkeleton() {
/* 225 */     return this.skeleton;
/*     */   }
/*     */ 
/*     */   
/*     */   public Bone getParent() {
/* 230 */     return this.parent;
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<Bone> getChildren() {
/* 235 */     return this.children;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isActive() {
/* 241 */     return this.active;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getX() {
/* 248 */     return this.x;
/*     */   }
/*     */   
/*     */   public void setX(float x) {
/* 252 */     this.x = x;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getY() {
/* 257 */     return this.y;
/*     */   }
/*     */   
/*     */   public void setY(float y) {
/* 261 */     this.y = y;
/*     */   }
/*     */   
/*     */   public void setPosition(float x, float y) {
/* 265 */     this.x = x;
/* 266 */     this.y = y;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getRotation() {
/* 271 */     return this.rotation;
/*     */   }
/*     */   
/*     */   public void setRotation(float rotation) {
/* 275 */     this.rotation = rotation;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getScaleX() {
/* 280 */     return this.scaleX;
/*     */   }
/*     */   
/*     */   public void setScaleX(float scaleX) {
/* 284 */     this.scaleX = scaleX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getScaleY() {
/* 289 */     return this.scaleY;
/*     */   }
/*     */   
/*     */   public void setScaleY(float scaleY) {
/* 293 */     this.scaleY = scaleY;
/*     */   }
/*     */   
/*     */   public void setScale(float scaleX, float scaleY) {
/* 297 */     this.scaleX = scaleX;
/* 298 */     this.scaleY = scaleY;
/*     */   }
/*     */   
/*     */   public void setScale(float scale) {
/* 302 */     this.scaleX = scale;
/* 303 */     this.scaleY = scale;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getShearX() {
/* 308 */     return this.shearX;
/*     */   }
/*     */   
/*     */   public void setShearX(float shearX) {
/* 312 */     this.shearX = shearX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getShearY() {
/* 317 */     return this.shearY;
/*     */   }
/*     */   
/*     */   public void setShearY(float shearY) {
/* 321 */     this.shearY = shearY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAX() {
/* 328 */     return this.ax;
/*     */   }
/*     */   
/*     */   public void setAX(float ax) {
/* 332 */     this.ax = ax;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAY() {
/* 337 */     return this.ay;
/*     */   }
/*     */   
/*     */   public void setAY(float ay) {
/* 341 */     this.ay = ay;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getARotation() {
/* 346 */     return this.arotation;
/*     */   }
/*     */   
/*     */   public void setARotation(float arotation) {
/* 350 */     this.arotation = arotation;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAScaleX() {
/* 355 */     return this.ascaleX;
/*     */   }
/*     */   
/*     */   public void setAScaleX(float ascaleX) {
/* 359 */     this.ascaleX = ascaleX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAScaleY() {
/* 364 */     return this.ascaleY;
/*     */   }
/*     */   
/*     */   public void setAScaleY(float ascaleY) {
/* 368 */     this.ascaleY = ascaleY;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAShearX() {
/* 373 */     return this.ashearX;
/*     */   }
/*     */   
/*     */   public void setAShearX(float ashearX) {
/* 377 */     this.ashearX = ashearX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getAShearY() {
/* 382 */     return this.ashearY;
/*     */   }
/*     */   
/*     */   public void setAShearY(float ashearY) {
/* 386 */     this.ashearY = ashearY;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAppliedValid() {
/* 392 */     return this.appliedValid;
/*     */   }
/*     */   
/*     */   public void setAppliedValid(boolean appliedValid) {
/* 396 */     this.appliedValid = appliedValid;
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
/*     */   public void updateAppliedTransform() {
/* 408 */     this.appliedValid = true;
/* 409 */     Bone parent = this.parent;
/* 410 */     if (parent == null) {
/* 411 */       this.ax = this.worldX;
/* 412 */       this.ay = this.worldY;
/* 413 */       this.arotation = SpineUtils.atan2(this.c, this.a) * 57.295776F;
/* 414 */       this.ascaleX = (float)Math.sqrt((this.a * this.a + this.c * this.c));
/* 415 */       this.ascaleY = (float)Math.sqrt((this.b * this.b + this.d * this.d));
/* 416 */       this.ashearX = 0.0F;
/* 417 */       this.ashearY = SpineUtils.atan2(this.a * this.b + this.c * this.d, this.a * this.d - this.b * this.c) * 57.295776F;
/*     */       return;
/*     */     } 
/* 420 */     float pa = parent.a, pb = parent.b, pc = parent.c, pd = parent.d;
/* 421 */     float pid = 1.0F / (pa * pd - pb * pc);
/* 422 */     float dx = this.worldX - parent.worldX, dy = this.worldY - parent.worldY;
/* 423 */     this.ax = dx * pd * pid - dy * pb * pid;
/* 424 */     this.ay = dy * pa * pid - dx * pc * pid;
/* 425 */     float ia = pid * pd;
/* 426 */     float id = pid * pa;
/* 427 */     float ib = pid * pb;
/* 428 */     float ic = pid * pc;
/* 429 */     float ra = ia * this.a - ib * this.c;
/* 430 */     float rb = ia * this.b - ib * this.d;
/* 431 */     float rc = id * this.c - ic * this.a;
/* 432 */     float rd = id * this.d - ic * this.b;
/* 433 */     this.ashearX = 0.0F;
/* 434 */     this.ascaleX = (float)Math.sqrt((ra * ra + rc * rc));
/* 435 */     if (this.ascaleX > 1.0E-4F) {
/* 436 */       float det = ra * rd - rb * rc;
/* 437 */       this.ascaleY = det / this.ascaleX;
/* 438 */       this.ashearY = SpineUtils.atan2(ra * rb + rc * rd, det) * 57.295776F;
/* 439 */       this.arotation = SpineUtils.atan2(rc, ra) * 57.295776F;
/*     */     } else {
/* 441 */       this.ascaleX = 0.0F;
/* 442 */       this.ascaleY = (float)Math.sqrt((rb * rb + rd * rd));
/* 443 */       this.ashearY = 0.0F;
/* 444 */       this.arotation = 90.0F - SpineUtils.atan2(rd, rb) * 57.295776F;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getA() {
/* 452 */     return this.a;
/*     */   }
/*     */   
/*     */   public void setA(float a) {
/* 456 */     this.a = a;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getB() {
/* 461 */     return this.b;
/*     */   }
/*     */   
/*     */   public void setB(float b) {
/* 465 */     this.b = b;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getC() {
/* 470 */     return this.c;
/*     */   }
/*     */   
/*     */   public void setC(float c) {
/* 474 */     this.c = c;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getD() {
/* 479 */     return this.d;
/*     */   }
/*     */   
/*     */   public void setD(float d) {
/* 483 */     this.d = d;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getWorldX() {
/* 488 */     return this.worldX;
/*     */   }
/*     */   
/*     */   public void setWorldX(float worldX) {
/* 492 */     this.worldX = worldX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getWorldY() {
/* 497 */     return this.worldY;
/*     */   }
/*     */   
/*     */   public void setWorldY(float worldY) {
/* 501 */     this.worldY = worldY;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getWorldRotationX() {
/* 506 */     return SpineUtils.atan2(this.c, this.a) * 57.295776F;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getWorldRotationY() {
/* 511 */     return SpineUtils.atan2(this.d, this.b) * 57.295776F;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getWorldScaleX() {
/* 516 */     return (float)Math.sqrt((this.a * this.a + this.c * this.c));
/*     */   }
/*     */ 
/*     */   
/*     */   public float getWorldScaleY() {
/* 521 */     return (float)Math.sqrt((this.b * this.b + this.d * this.d));
/*     */   }
/*     */   
/*     */   public Matrix3 getWorldTransform(Matrix3 worldTransform) {
/* 525 */     if (worldTransform == null) throw new IllegalArgumentException("worldTransform cannot be null."); 
/* 526 */     float[] val = worldTransform.val;
/* 527 */     val[0] = this.a;
/* 528 */     val[3] = this.b;
/* 529 */     val[1] = this.c;
/* 530 */     val[4] = this.d;
/* 531 */     val[6] = this.worldX;
/* 532 */     val[7] = this.worldY;
/* 533 */     val[2] = 0.0F;
/* 534 */     val[5] = 0.0F;
/* 535 */     val[8] = 1.0F;
/* 536 */     return worldTransform;
/*     */   }
/*     */ 
/*     */   
/*     */   public Vector2 worldToLocal(Vector2 world) {
/* 541 */     if (world == null) throw new IllegalArgumentException("world cannot be null."); 
/* 542 */     float invDet = 1.0F / (this.a * this.d - this.b * this.c);
/* 543 */     float x = world.x - this.worldX, y = world.y - this.worldY;
/* 544 */     world.x = x * this.d * invDet - y * this.b * invDet;
/* 545 */     world.y = y * this.a * invDet - x * this.c * invDet;
/* 546 */     return world;
/*     */   }
/*     */ 
/*     */   
/*     */   public Vector2 localToWorld(Vector2 local) {
/* 551 */     if (local == null) throw new IllegalArgumentException("local cannot be null."); 
/* 552 */     float x = local.x, y = local.y;
/* 553 */     local.x = x * this.a + y * this.b + this.worldX;
/* 554 */     local.y = x * this.c + y * this.d + this.worldY;
/* 555 */     return local;
/*     */   }
/*     */ 
/*     */   
/*     */   public float worldToLocalRotation(float worldRotation) {
/* 560 */     float sin = SpineUtils.sinDeg(worldRotation), cos = SpineUtils.cosDeg(worldRotation);
/* 561 */     return SpineUtils.atan2(this.a * sin - this.c * cos, this.d * cos - this.b * sin) * 57.295776F + this.rotation - this.shearX;
/*     */   }
/*     */ 
/*     */   
/*     */   public float localToWorldRotation(float localRotation) {
/* 566 */     localRotation -= this.rotation - this.shearX;
/* 567 */     float sin = SpineUtils.sinDeg(localRotation), cos = SpineUtils.cosDeg(localRotation);
/* 568 */     return SpineUtils.atan2(cos * this.c + sin * this.d, cos * this.a + sin * this.b) * 57.295776F;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void rotateWorld(float degrees) {
/* 574 */     float cos = SpineUtils.cosDeg(degrees), sin = SpineUtils.sinDeg(degrees);
/* 575 */     this.a = cos * this.a - sin * this.c;
/* 576 */     this.b = cos * this.b - sin * this.d;
/* 577 */     this.c = sin * this.a + cos * this.c;
/* 578 */     this.d = sin * this.b + cos * this.d;
/* 579 */     this.appliedValid = false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 585 */     return this.data.name;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\Bone.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */