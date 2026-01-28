/*     */ package com.esotericsoftware.spine38;
/*     */ 
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
/*     */ public class TransformConstraint
/*     */   implements Updatable
/*     */ {
/*     */   final TransformConstraintData data;
/*     */   final Array<Bone> bones;
/*     */   Bone target;
/*     */   float rotateMix;
/*     */   float translateMix;
/*     */   float scaleMix;
/*     */   float shearMix;
/*     */   boolean active;
/*  48 */   final Vector2 temp = new Vector2();
/*     */   
/*     */   public TransformConstraint(TransformConstraintData data, Skeleton skeleton) {
/*  51 */     if (data == null) throw new IllegalArgumentException("data cannot be null."); 
/*  52 */     if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null."); 
/*  53 */     this.data = data;
/*  54 */     this.rotateMix = data.rotateMix;
/*  55 */     this.translateMix = data.translateMix;
/*  56 */     this.scaleMix = data.scaleMix;
/*  57 */     this.shearMix = data.shearMix;
/*  58 */     this.bones = new Array(data.bones.size);
/*  59 */     for (BoneData boneData : data.bones)
/*  60 */       this.bones.add(skeleton.findBone(boneData.name)); 
/*  61 */     this.target = skeleton.findBone(data.target.name);
/*     */   }
/*     */ 
/*     */   
/*     */   public TransformConstraint(TransformConstraint constraint, Skeleton skeleton) {
/*  66 */     if (constraint == null) throw new IllegalArgumentException("constraint cannot be null."); 
/*  67 */     if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null."); 
/*  68 */     this.data = constraint.data;
/*  69 */     this.bones = new Array(constraint.bones.size);
/*  70 */     for (Bone bone : constraint.bones)
/*  71 */       this.bones.add(skeleton.bones.get(bone.data.index)); 
/*  72 */     this.target = (Bone)skeleton.bones.get(constraint.target.data.index);
/*  73 */     this.rotateMix = constraint.rotateMix;
/*  74 */     this.translateMix = constraint.translateMix;
/*  75 */     this.scaleMix = constraint.scaleMix;
/*  76 */     this.shearMix = constraint.shearMix;
/*     */   }
/*     */ 
/*     */   
/*     */   public void apply() {
/*  81 */     update();
/*     */   }
/*     */   
/*     */   public void update() {
/*  85 */     if (this.data.local) {
/*  86 */       if (this.data.relative) {
/*  87 */         applyRelativeLocal();
/*     */       } else {
/*  89 */         applyAbsoluteLocal();
/*     */       } 
/*  91 */     } else if (this.data.relative) {
/*  92 */       applyRelativeWorld();
/*     */     } else {
/*  94 */       applyAbsoluteWorld();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void applyAbsoluteWorld() {
/*  99 */     float rotateMix = this.rotateMix, translateMix = this.translateMix, scaleMix = this.scaleMix, shearMix = this.shearMix;
/* 100 */     Bone target = this.target;
/* 101 */     float ta = target.a, tb = target.b, tc = target.c, td = target.d;
/* 102 */     float degRadReflect = (ta * td - tb * tc > 0.0F) ? 0.017453292F : -0.017453292F;
/* 103 */     float offsetRotation = this.data.offsetRotation * degRadReflect, offsetShearY = this.data.offsetShearY * degRadReflect;
/* 104 */     Array<Bone> bones = this.bones;
/* 105 */     for (int i = 0, n = bones.size; i < n; i++) {
/* 106 */       Bone bone = (Bone)bones.get(i);
/* 107 */       boolean modified = false;
/*     */       
/* 109 */       if (rotateMix != 0.0F) {
/* 110 */         float a = bone.a, b = bone.b, c = bone.c, d = bone.d;
/* 111 */         float r = SpineUtils.atan2(tc, ta) - SpineUtils.atan2(c, a) + offsetRotation;
/* 112 */         if (r > 3.1415927F)
/* 113 */         { r -= 6.2831855F; }
/* 114 */         else if (r < -3.1415927F) { r += 6.2831855F; }
/* 115 */          r *= rotateMix;
/* 116 */         float cos = SpineUtils.cos(r), sin = SpineUtils.sin(r);
/* 117 */         bone.a = cos * a - sin * c;
/* 118 */         bone.b = cos * b - sin * d;
/* 119 */         bone.c = sin * a + cos * c;
/* 120 */         bone.d = sin * b + cos * d;
/* 121 */         modified = true;
/*     */       } 
/*     */       
/* 124 */       if (translateMix != 0.0F) {
/* 125 */         Vector2 temp = this.temp;
/* 126 */         target.localToWorld(temp.set(this.data.offsetX, this.data.offsetY));
/* 127 */         bone.worldX += (temp.x - bone.worldX) * translateMix;
/* 128 */         bone.worldY += (temp.y - bone.worldY) * translateMix;
/* 129 */         modified = true;
/*     */       } 
/*     */       
/* 132 */       if (scaleMix > 0.0F) {
/* 133 */         float s = (float)Math.sqrt((bone.a * bone.a + bone.c * bone.c));
/* 134 */         if (s != 0.0F) s = (s + ((float)Math.sqrt((ta * ta + tc * tc)) - s + this.data.offsetScaleX) * scaleMix) / s; 
/* 135 */         bone.a *= s;
/* 136 */         bone.c *= s;
/* 137 */         s = (float)Math.sqrt((bone.b * bone.b + bone.d * bone.d));
/* 138 */         if (s != 0.0F) s = (s + ((float)Math.sqrt((tb * tb + td * td)) - s + this.data.offsetScaleY) * scaleMix) / s; 
/* 139 */         bone.b *= s;
/* 140 */         bone.d *= s;
/* 141 */         modified = true;
/*     */       } 
/*     */       
/* 144 */       if (shearMix > 0.0F) {
/* 145 */         float b = bone.b, d = bone.d;
/* 146 */         float by = SpineUtils.atan2(d, b);
/* 147 */         float r = SpineUtils.atan2(td, tb) - SpineUtils.atan2(tc, ta) - by - SpineUtils.atan2(bone.c, bone.a);
/* 148 */         if (r > 3.1415927F)
/* 149 */         { r -= 6.2831855F; }
/* 150 */         else if (r < -3.1415927F) { r += 6.2831855F; }
/* 151 */          r = by + (r + offsetShearY) * shearMix;
/* 152 */         float s = (float)Math.sqrt((b * b + d * d));
/* 153 */         bone.b = SpineUtils.cos(r) * s;
/* 154 */         bone.d = SpineUtils.sin(r) * s;
/* 155 */         modified = true;
/*     */       } 
/*     */       
/* 158 */       if (modified) bone.appliedValid = false; 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void applyRelativeWorld() {
/* 163 */     float rotateMix = this.rotateMix, translateMix = this.translateMix, scaleMix = this.scaleMix, shearMix = this.shearMix;
/* 164 */     Bone target = this.target;
/* 165 */     float ta = target.a, tb = target.b, tc = target.c, td = target.d;
/* 166 */     float degRadReflect = (ta * td - tb * tc > 0.0F) ? 0.017453292F : -0.017453292F;
/* 167 */     float offsetRotation = this.data.offsetRotation * degRadReflect, offsetShearY = this.data.offsetShearY * degRadReflect;
/* 168 */     Array<Bone> bones = this.bones;
/* 169 */     for (int i = 0, n = bones.size; i < n; i++) {
/* 170 */       Bone bone = (Bone)bones.get(i);
/* 171 */       boolean modified = false;
/*     */       
/* 173 */       if (rotateMix != 0.0F) {
/* 174 */         float a = bone.a, b = bone.b, c = bone.c, d = bone.d;
/* 175 */         float r = SpineUtils.atan2(tc, ta) + offsetRotation;
/* 176 */         if (r > 3.1415927F)
/* 177 */         { r -= 6.2831855F; }
/* 178 */         else if (r < -3.1415927F) { r += 6.2831855F; }
/* 179 */          r *= rotateMix;
/* 180 */         float cos = SpineUtils.cos(r), sin = SpineUtils.sin(r);
/* 181 */         bone.a = cos * a - sin * c;
/* 182 */         bone.b = cos * b - sin * d;
/* 183 */         bone.c = sin * a + cos * c;
/* 184 */         bone.d = sin * b + cos * d;
/* 185 */         modified = true;
/*     */       } 
/*     */       
/* 188 */       if (translateMix != 0.0F) {
/* 189 */         Vector2 temp = this.temp;
/* 190 */         target.localToWorld(temp.set(this.data.offsetX, this.data.offsetY));
/* 191 */         bone.worldX += temp.x * translateMix;
/* 192 */         bone.worldY += temp.y * translateMix;
/* 193 */         modified = true;
/*     */       } 
/*     */       
/* 196 */       if (scaleMix > 0.0F) {
/* 197 */         float s = ((float)Math.sqrt((ta * ta + tc * tc)) - 1.0F + this.data.offsetScaleX) * scaleMix + 1.0F;
/* 198 */         bone.a *= s;
/* 199 */         bone.c *= s;
/* 200 */         s = ((float)Math.sqrt((tb * tb + td * td)) - 1.0F + this.data.offsetScaleY) * scaleMix + 1.0F;
/* 201 */         bone.b *= s;
/* 202 */         bone.d *= s;
/* 203 */         modified = true;
/*     */       } 
/*     */       
/* 206 */       if (shearMix > 0.0F) {
/* 207 */         float r = SpineUtils.atan2(td, tb) - SpineUtils.atan2(tc, ta);
/* 208 */         if (r > 3.1415927F)
/* 209 */         { r -= 6.2831855F; }
/* 210 */         else if (r < -3.1415927F) { r += 6.2831855F; }
/* 211 */          float b = bone.b, d = bone.d;
/* 212 */         r = SpineUtils.atan2(d, b) + (r - 1.5707964F + offsetShearY) * shearMix;
/* 213 */         float s = (float)Math.sqrt((b * b + d * d));
/* 214 */         bone.b = SpineUtils.cos(r) * s;
/* 215 */         bone.d = SpineUtils.sin(r) * s;
/* 216 */         modified = true;
/*     */       } 
/*     */       
/* 219 */       if (modified) bone.appliedValid = false; 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void applyAbsoluteLocal() {
/* 224 */     float rotateMix = this.rotateMix, translateMix = this.translateMix, scaleMix = this.scaleMix, shearMix = this.shearMix;
/* 225 */     Bone target = this.target;
/* 226 */     if (!target.appliedValid) target.updateAppliedTransform(); 
/* 227 */     Array<Bone> bones = this.bones;
/* 228 */     for (int i = 0, n = bones.size; i < n; i++) {
/* 229 */       Bone bone = (Bone)bones.get(i);
/* 230 */       if (!bone.appliedValid) bone.updateAppliedTransform();
/*     */       
/* 232 */       float rotation = bone.arotation;
/* 233 */       if (rotateMix != 0.0F) {
/* 234 */         float r = target.arotation - rotation + this.data.offsetRotation;
/* 235 */         r -= ((16384 - (int)(16384.499999999996D - (r / 360.0F))) * 360);
/* 236 */         rotation += r * rotateMix;
/*     */       } 
/*     */       
/* 239 */       float x = bone.ax, y = bone.ay;
/* 240 */       if (translateMix != 0.0F) {
/* 241 */         x += (target.ax - x + this.data.offsetX) * translateMix;
/* 242 */         y += (target.ay - y + this.data.offsetY) * translateMix;
/*     */       } 
/*     */       
/* 245 */       float scaleX = bone.ascaleX, scaleY = bone.ascaleY;
/* 246 */       if (scaleMix != 0.0F) {
/* 247 */         if (scaleX != 0.0F) scaleX = (scaleX + (target.ascaleX - scaleX + this.data.offsetScaleX) * scaleMix) / scaleX; 
/* 248 */         if (scaleY != 0.0F) scaleY = (scaleY + (target.ascaleY - scaleY + this.data.offsetScaleY) * scaleMix) / scaleY;
/*     */       
/*     */       } 
/* 251 */       float shearY = bone.ashearY;
/* 252 */       if (shearMix != 0.0F) {
/* 253 */         float r = target.ashearY - shearY + this.data.offsetShearY;
/* 254 */         r -= ((16384 - (int)(16384.499999999996D - (r / 360.0F))) * 360);
/* 255 */         shearY += r * shearMix;
/*     */       } 
/*     */       
/* 258 */       bone.updateWorldTransform(x, y, rotation, scaleX, scaleY, bone.ashearX, shearY);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void applyRelativeLocal() {
/* 263 */     float rotateMix = this.rotateMix, translateMix = this.translateMix, scaleMix = this.scaleMix, shearMix = this.shearMix;
/* 264 */     Bone target = this.target;
/* 265 */     if (!target.appliedValid) target.updateAppliedTransform(); 
/* 266 */     Array<Bone> bones = this.bones;
/* 267 */     for (int i = 0, n = bones.size; i < n; i++) {
/* 268 */       Bone bone = (Bone)bones.get(i);
/* 269 */       if (!bone.appliedValid) bone.updateAppliedTransform();
/*     */       
/* 271 */       float rotation = bone.arotation;
/* 272 */       if (rotateMix != 0.0F) rotation += (target.arotation + this.data.offsetRotation) * rotateMix;
/*     */       
/* 274 */       float x = bone.ax, y = bone.ay;
/* 275 */       if (translateMix != 0.0F) {
/* 276 */         x += (target.ax + this.data.offsetX) * translateMix;
/* 277 */         y += (target.ay + this.data.offsetY) * translateMix;
/*     */       } 
/*     */       
/* 280 */       float scaleX = bone.ascaleX, scaleY = bone.ascaleY;
/* 281 */       if (scaleMix != 0.0F) {
/* 282 */         scaleX *= (target.ascaleX - 1.0F + this.data.offsetScaleX) * scaleMix + 1.0F;
/* 283 */         scaleY *= (target.ascaleY - 1.0F + this.data.offsetScaleY) * scaleMix + 1.0F;
/*     */       } 
/*     */       
/* 286 */       float shearY = bone.ashearY;
/* 287 */       if (shearMix != 0.0F) shearY += (target.ashearY + this.data.offsetShearY) * shearMix;
/*     */       
/* 289 */       bone.updateWorldTransform(x, y, rotation, scaleX, scaleY, bone.ashearX, shearY);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<Bone> getBones() {
/* 295 */     return this.bones;
/*     */   }
/*     */ 
/*     */   
/*     */   public Bone getTarget() {
/* 300 */     return this.target;
/*     */   }
/*     */   
/*     */   public void setTarget(Bone target) {
/* 304 */     if (target == null) throw new IllegalArgumentException("target cannot be null."); 
/* 305 */     this.target = target;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getRotateMix() {
/* 310 */     return this.rotateMix;
/*     */   }
/*     */   
/*     */   public void setRotateMix(float rotateMix) {
/* 314 */     this.rotateMix = rotateMix;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getTranslateMix() {
/* 319 */     return this.translateMix;
/*     */   }
/*     */   
/*     */   public void setTranslateMix(float translateMix) {
/* 323 */     this.translateMix = translateMix;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getScaleMix() {
/* 328 */     return this.scaleMix;
/*     */   }
/*     */   
/*     */   public void setScaleMix(float scaleMix) {
/* 332 */     this.scaleMix = scaleMix;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getShearMix() {
/* 337 */     return this.shearMix;
/*     */   }
/*     */   
/*     */   public void setShearMix(float shearMix) {
/* 341 */     this.shearMix = shearMix;
/*     */   }
/*     */   
/*     */   public boolean isActive() {
/* 345 */     return this.active;
/*     */   }
/*     */ 
/*     */   
/*     */   public TransformConstraintData getData() {
/* 350 */     return this.data;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 354 */     return this.data.name;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\TransformConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */