/*     */ package com.esotericsoftware.spine38;
/*     */ 
/*     */ import com.badlogic.gdx.utils.Array;
/*     */ import com.badlogic.gdx.utils.FloatArray;
/*     */ import com.esotericsoftware.spine38.attachments.Attachment;
/*     */ import com.esotericsoftware.spine38.attachments.PathAttachment;
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
/*     */ public class PathConstraint
/*     */   implements Updatable
/*     */ {
/*     */   private static final int NONE = -1;
/*     */   private static final int BEFORE = -2;
/*     */   private static final int AFTER = -3;
/*     */   private static final float epsilon = 1.0E-5F;
/*     */   final PathConstraintData data;
/*     */   final Array<Bone> bones;
/*     */   Slot target;
/*     */   float position;
/*     */   float spacing;
/*     */   float rotateMix;
/*     */   float translateMix;
/*     */   boolean active;
/*  56 */   private final FloatArray spaces = new FloatArray(), positions = new FloatArray();
/*  57 */   private final FloatArray world = new FloatArray(); private final FloatArray curves = new FloatArray(); private final FloatArray lengths = new FloatArray();
/*  58 */   private final float[] segments = new float[10];
/*     */   
/*     */   public PathConstraint(PathConstraintData data, Skeleton skeleton) {
/*  61 */     if (data == null) throw new IllegalArgumentException("data cannot be null."); 
/*  62 */     if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null."); 
/*  63 */     this.data = data;
/*  64 */     this.bones = new Array(data.bones.size);
/*  65 */     for (BoneData boneData : data.bones)
/*  66 */       this.bones.add(skeleton.findBone(boneData.name)); 
/*  67 */     this.target = skeleton.findSlot(data.target.name);
/*  68 */     this.position = data.position;
/*  69 */     this.spacing = data.spacing;
/*  70 */     this.rotateMix = data.rotateMix;
/*  71 */     this.translateMix = data.translateMix;
/*     */   }
/*     */ 
/*     */   
/*     */   public PathConstraint(PathConstraint constraint, Skeleton skeleton) {
/*  76 */     if (constraint == null) throw new IllegalArgumentException("constraint cannot be null."); 
/*  77 */     if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null."); 
/*  78 */     this.data = constraint.data;
/*  79 */     this.bones = new Array(constraint.bones.size);
/*  80 */     for (Bone bone : constraint.bones)
/*  81 */       this.bones.add(skeleton.bones.get(bone.data.index)); 
/*  82 */     this.target = (Slot)skeleton.slots.get(constraint.target.data.index);
/*  83 */     this.position = constraint.position;
/*  84 */     this.spacing = constraint.spacing;
/*  85 */     this.rotateMix = constraint.rotateMix;
/*  86 */     this.translateMix = constraint.translateMix;
/*     */   }
/*     */ 
/*     */   
/*     */   public void apply() {
/*  91 */     update();
/*     */   }
/*     */   
/*     */   public void update() {
/*     */     boolean tip;
/*  96 */     Attachment attachment = this.target.attachment;
/*  97 */     if (!(attachment instanceof PathAttachment))
/*     */       return; 
/*  99 */     float rotateMix = this.rotateMix, translateMix = this.translateMix;
/* 100 */     boolean translate = (translateMix > 0.0F), rotate = (rotateMix > 0.0F);
/* 101 */     if (!translate && !rotate)
/*     */       return; 
/* 103 */     PathConstraintData data = this.data;
/* 104 */     boolean percentSpacing = (data.spacingMode == PathConstraintData.SpacingMode.percent);
/* 105 */     PathConstraintData.RotateMode rotateMode = data.rotateMode;
/* 106 */     boolean tangents = (rotateMode == PathConstraintData.RotateMode.tangent), scale = (rotateMode == PathConstraintData.RotateMode.chainScale);
/* 107 */     int boneCount = this.bones.size, spacesCount = tangents ? boneCount : (boneCount + 1);
/* 108 */     Object[] bones = this.bones.items;
/* 109 */     float[] spaces = this.spaces.setSize(spacesCount), lengths = null;
/* 110 */     float spacing = this.spacing;
/* 111 */     if (scale || !percentSpacing) {
/* 112 */       if (scale) lengths = this.lengths.setSize(boneCount); 
/* 113 */       boolean lengthSpacing = (data.spacingMode == PathConstraintData.SpacingMode.length);
/* 114 */       for (int j = 0, n = spacesCount - 1; j < n; ) {
/* 115 */         Bone bone = (Bone)bones[j];
/* 116 */         float setupLength = bone.data.length;
/* 117 */         if (setupLength < 1.0E-5F) {
/* 118 */           if (scale) lengths[j] = 0.0F; 
/* 119 */           spaces[++j] = 0.0F; continue;
/* 120 */         }  if (percentSpacing) {
/* 121 */           if (scale) {
/* 122 */             float f1 = setupLength * bone.a, f2 = setupLength * bone.c;
/* 123 */             float f3 = (float)Math.sqrt((f1 * f1 + f2 * f2));
/* 124 */             lengths[j] = f3;
/*     */           } 
/* 126 */           spaces[++j] = spacing; continue;
/*     */         } 
/* 128 */         float x = setupLength * bone.a, y = setupLength * bone.c;
/* 129 */         float length = (float)Math.sqrt((x * x + y * y));
/* 130 */         if (scale) lengths[j] = length; 
/* 131 */         spaces[++j] = (lengthSpacing ? (setupLength + spacing) : spacing) * length / setupLength;
/*     */       } 
/*     */     } else {
/*     */       
/* 135 */       for (int j = 1; j < spacesCount; j++) {
/* 136 */         spaces[j] = spacing;
/*     */       }
/*     */     } 
/* 139 */     float[] positions = computeWorldPositions((PathAttachment)attachment, spacesCount, tangents, (data.positionMode == PathConstraintData.PositionMode.percent), percentSpacing);
/*     */     
/* 141 */     float boneX = positions[0], boneY = positions[1], offsetRotation = data.offsetRotation;
/*     */     
/* 143 */     if (offsetRotation == 0.0F) {
/* 144 */       tip = (rotateMode == PathConstraintData.RotateMode.chain);
/*     */     } else {
/* 146 */       tip = false;
/* 147 */       Bone bone = this.target.bone;
/* 148 */       offsetRotation *= (bone.a * bone.d - bone.b * bone.c > 0.0F) ? 0.017453292F : -0.017453292F;
/*     */     } 
/* 150 */     for (int i = 0, p = 3; i < boneCount; i++, p += 3) {
/* 151 */       Bone bone = (Bone)bones[i];
/* 152 */       bone.worldX += (boneX - bone.worldX) * translateMix;
/* 153 */       bone.worldY += (boneY - bone.worldY) * translateMix;
/* 154 */       float x = positions[p], y = positions[p + 1], dx = x - boneX, dy = y - boneY;
/* 155 */       if (scale) {
/* 156 */         float length = lengths[i];
/* 157 */         if (length >= 1.0E-5F) {
/* 158 */           float s = ((float)Math.sqrt((dx * dx + dy * dy)) / length - 1.0F) * rotateMix + 1.0F;
/* 159 */           bone.a *= s;
/* 160 */           bone.c *= s;
/*     */         } 
/*     */       } 
/* 163 */       boneX = x;
/* 164 */       boneY = y;
/* 165 */       if (rotate) {
/* 166 */         float r, a = bone.a, b = bone.b, c = bone.c, d = bone.d;
/* 167 */         if (tangents) {
/* 168 */           r = positions[p - 1];
/* 169 */         } else if (spaces[i + 1] < 1.0E-5F) {
/* 170 */           r = positions[p + 2];
/*     */         } else {
/* 172 */           r = (float)Math.atan2(dy, dx);
/* 173 */         }  r -= (float)Math.atan2(c, a);
/* 174 */         if (tip) {
/* 175 */           float f1 = (float)Math.cos(r);
/* 176 */           float f2 = (float)Math.sin(r);
/* 177 */           float length = bone.data.length;
/* 178 */           boneX += (length * (f1 * a - f2 * c) - dx) * rotateMix;
/* 179 */           boneY += (length * (f2 * a + f1 * c) - dy) * rotateMix;
/*     */         } else {
/* 181 */           r += offsetRotation;
/* 182 */         }  if (r > 3.1415927F) {
/* 183 */           r -= 6.2831855F;
/* 184 */         } else if (r < -3.1415927F) {
/* 185 */           r += 6.2831855F;
/* 186 */         }  r *= rotateMix;
/* 187 */         float cos = (float)Math.cos(r);
/* 188 */         float sin = (float)Math.sin(r);
/* 189 */         bone.a = cos * a - sin * c;
/* 190 */         bone.b = cos * b - sin * d;
/* 191 */         bone.c = sin * a + cos * c;
/* 192 */         bone.d = sin * b + cos * d;
/*     */       } 
/* 194 */       bone.appliedValid = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   float[] computeWorldPositions(PathAttachment path, int spacesCount, boolean tangents, boolean percentPosition, boolean percentSpacing) {
/*     */     float[] world;
/* 200 */     Slot target = this.target;
/* 201 */     float position = this.position;
/* 202 */     float[] spaces = this.spaces.items, out = this.positions.setSize(spacesCount * 3 + 2);
/* 203 */     boolean closed = path.getClosed();
/* 204 */     int verticesLength = path.getWorldVerticesLength(), curveCount = verticesLength / 6, prevCurve = -1;
/*     */     
/* 206 */     if (!path.getConstantSpeed()) {
/* 207 */       float[] lengths = path.getLengths();
/* 208 */       curveCount -= closed ? 1 : 2;
/* 209 */       float f = lengths[curveCount];
/* 210 */       if (percentPosition) position *= f; 
/* 211 */       if (percentSpacing)
/* 212 */         for (int i1 = 1; i1 < spacesCount; i1++) {
/* 213 */           spaces[i1] = spaces[i1] * f;
/*     */         } 
/* 215 */       world = this.world.setSize(8);
/* 216 */       for (int k = 0, m = 0, n = 0; k < spacesCount; k++, m += 3) {
/* 217 */         float space = spaces[k];
/* 218 */         position += space;
/* 219 */         float p = position;
/*     */         
/* 221 */         if (closed)
/* 222 */         { p %= f;
/* 223 */           if (p < 0.0F) p += f; 
/* 224 */           n = 0; }
/* 225 */         else { if (p < 0.0F) {
/* 226 */             if (prevCurve != -2) {
/* 227 */               prevCurve = -2;
/* 228 */               path.computeWorldVertices(target, 2, 4, world, 0, 2);
/*     */             } 
/* 230 */             addBeforePosition(p, world, 0, out, m); continue;
/*     */           } 
/* 232 */           if (p > f) {
/* 233 */             if (prevCurve != -3) {
/* 234 */               prevCurve = -3;
/* 235 */               path.computeWorldVertices(target, verticesLength - 6, 4, world, 0, 2);
/*     */             } 
/* 237 */             addAfterPosition(p - f, world, 0, out, m);
/*     */             
/*     */             continue;
/*     */           }  }
/*     */         
/*     */         while (true) {
/* 243 */           float length = lengths[n];
/* 244 */           if (p > length) { n++; continue; }
/* 245 */            if (n == 0) {
/* 246 */             p /= length; break;
/*     */           } 
/* 248 */           float prev = lengths[n - 1];
/* 249 */           p = (p - prev) / (length - prev);
/*     */           
/*     */           break;
/*     */         } 
/* 253 */         if (n != prevCurve) {
/* 254 */           prevCurve = n;
/* 255 */           if (closed && n == curveCount) {
/* 256 */             path.computeWorldVertices(target, verticesLength - 4, 4, world, 0, 2);
/* 257 */             path.computeWorldVertices(target, 0, 4, world, 4, 2);
/*     */           } else {
/* 259 */             path.computeWorldVertices(target, n * 6 + 2, 8, world, 0, 2);
/*     */           } 
/* 261 */         }  addCurvePosition(p, world[0], world[1], world[2], world[3], world[4], world[5], world[6], world[7], out, m, (tangents || (k > 0 && space < 1.0E-5F)));
/*     */         continue;
/*     */       } 
/* 264 */       return out;
/*     */     } 
/*     */ 
/*     */     
/* 268 */     if (closed) {
/* 269 */       verticesLength += 2;
/* 270 */       world = this.world.setSize(verticesLength);
/* 271 */       path.computeWorldVertices(target, 2, verticesLength - 4, world, 0, 2);
/* 272 */       path.computeWorldVertices(target, 0, 2, world, verticesLength - 4, 2);
/* 273 */       world[verticesLength - 2] = world[0];
/* 274 */       world[verticesLength - 1] = world[1];
/*     */     } else {
/* 276 */       curveCount--;
/* 277 */       verticesLength -= 4;
/* 278 */       world = this.world.setSize(verticesLength);
/* 279 */       path.computeWorldVertices(target, 2, verticesLength, world, 0, 2);
/*     */     } 
/*     */ 
/*     */     
/* 283 */     float[] curves = this.curves.setSize(curveCount);
/* 284 */     float pathLength = 0.0F;
/* 285 */     float x1 = world[0], y1 = world[1], cx1 = 0.0F, cy1 = 0.0F, cx2 = 0.0F, cy2 = 0.0F, x2 = 0.0F, y2 = 0.0F;
/*     */     int i, w;
/* 287 */     for (i = 0, w = 2; i < curveCount; i++, w += 6) {
/* 288 */       cx1 = world[w];
/* 289 */       cy1 = world[w + 1];
/* 290 */       cx2 = world[w + 2];
/* 291 */       cy2 = world[w + 3];
/* 292 */       x2 = world[w + 4];
/* 293 */       y2 = world[w + 5];
/* 294 */       float tmpx = (x1 - cx1 * 2.0F + cx2) * 0.1875F;
/* 295 */       float tmpy = (y1 - cy1 * 2.0F + cy2) * 0.1875F;
/* 296 */       float dddfx = ((cx1 - cx2) * 3.0F - x1 + x2) * 0.09375F;
/* 297 */       float dddfy = ((cy1 - cy2) * 3.0F - y1 + y2) * 0.09375F;
/* 298 */       float ddfx = tmpx * 2.0F + dddfx;
/* 299 */       float ddfy = tmpy * 2.0F + dddfy;
/* 300 */       float dfx = (cx1 - x1) * 0.75F + tmpx + dddfx * 0.16666667F;
/* 301 */       float dfy = (cy1 - y1) * 0.75F + tmpy + dddfy * 0.16666667F;
/* 302 */       pathLength += (float)Math.sqrt((dfx * dfx + dfy * dfy));
/* 303 */       dfx += ddfx;
/* 304 */       dfy += ddfy;
/* 305 */       ddfx += dddfx;
/* 306 */       ddfy += dddfy;
/* 307 */       pathLength += (float)Math.sqrt((dfx * dfx + dfy * dfy));
/* 308 */       dfx += ddfx;
/* 309 */       dfy += ddfy;
/* 310 */       pathLength += (float)Math.sqrt((dfx * dfx + dfy * dfy));
/* 311 */       dfx += ddfx + dddfx;
/* 312 */       dfy += ddfy + dddfy;
/* 313 */       pathLength += (float)Math.sqrt((dfx * dfx + dfy * dfy));
/* 314 */       curves[i] = pathLength;
/* 315 */       x1 = x2;
/* 316 */       y1 = y2;
/*     */     } 
/* 318 */     if (percentPosition) {
/* 319 */       position *= pathLength;
/*     */     } else {
/* 321 */       position *= pathLength / path.getLengths()[curveCount - 1];
/* 322 */     }  if (percentSpacing) {
/* 323 */       for (i = 1; i < spacesCount; i++) {
/* 324 */         spaces[i] = spaces[i] * pathLength;
/*     */       }
/*     */     }
/* 327 */     float[] segments = this.segments;
/* 328 */     float curveLength = 0.0F;
/* 329 */     for (int j = 0, o = 0, curve = 0, segment = 0; j < spacesCount; j++, o += 3) {
/* 330 */       float space = spaces[j];
/* 331 */       position += space;
/* 332 */       float p = position;
/*     */       
/* 334 */       if (closed)
/* 335 */       { p %= pathLength;
/* 336 */         if (p < 0.0F) p += pathLength; 
/* 337 */         curve = 0; }
/* 338 */       else { if (p < 0.0F) {
/* 339 */           addBeforePosition(p, world, 0, out, o); continue;
/*     */         } 
/* 341 */         if (p > pathLength) {
/* 342 */           addAfterPosition(p - pathLength, world, verticesLength - 4, out, o);
/*     */           
/*     */           continue;
/*     */         }  }
/*     */       
/*     */       while (true) {
/* 348 */         float length = curves[curve];
/* 349 */         if (p > length) { curve++; continue; }
/* 350 */          if (curve == 0) {
/* 351 */           p /= length; break;
/*     */         } 
/* 353 */         float prev = curves[curve - 1];
/* 354 */         p = (p - prev) / (length - prev);
/*     */ 
/*     */         
/*     */         break;
/*     */       } 
/*     */       
/* 360 */       if (curve != prevCurve) {
/* 361 */         prevCurve = curve;
/* 362 */         int ii = curve * 6;
/* 363 */         x1 = world[ii];
/* 364 */         y1 = world[ii + 1];
/* 365 */         cx1 = world[ii + 2];
/* 366 */         cy1 = world[ii + 3];
/* 367 */         cx2 = world[ii + 4];
/* 368 */         cy2 = world[ii + 5];
/* 369 */         x2 = world[ii + 6];
/* 370 */         y2 = world[ii + 7];
/* 371 */         float tmpx = (x1 - cx1 * 2.0F + cx2) * 0.03F;
/* 372 */         float tmpy = (y1 - cy1 * 2.0F + cy2) * 0.03F;
/* 373 */         float dddfx = ((cx1 - cx2) * 3.0F - x1 + x2) * 0.006F;
/* 374 */         float dddfy = ((cy1 - cy2) * 3.0F - y1 + y2) * 0.006F;
/* 375 */         float ddfx = tmpx * 2.0F + dddfx;
/* 376 */         float ddfy = tmpy * 2.0F + dddfy;
/* 377 */         float dfx = (cx1 - x1) * 0.3F + tmpx + dddfx * 0.16666667F;
/* 378 */         float dfy = (cy1 - y1) * 0.3F + tmpy + dddfy * 0.16666667F;
/* 379 */         curveLength = (float)Math.sqrt((dfx * dfx + dfy * dfy));
/* 380 */         segments[0] = curveLength;
/* 381 */         for (ii = 1; ii < 8; ii++) {
/* 382 */           dfx += ddfx;
/* 383 */           dfy += ddfy;
/* 384 */           ddfx += dddfx;
/* 385 */           ddfy += dddfy;
/* 386 */           curveLength += (float)Math.sqrt((dfx * dfx + dfy * dfy));
/* 387 */           segments[ii] = curveLength;
/*     */         } 
/* 389 */         dfx += ddfx;
/* 390 */         dfy += ddfy;
/* 391 */         curveLength += (float)Math.sqrt((dfx * dfx + dfy * dfy));
/* 392 */         segments[8] = curveLength;
/* 393 */         dfx += ddfx + dddfx;
/* 394 */         dfy += ddfy + dddfy;
/* 395 */         curveLength += (float)Math.sqrt((dfx * dfx + dfy * dfy));
/* 396 */         segments[9] = curveLength;
/* 397 */         segment = 0;
/*     */       } 
/*     */ 
/*     */       
/* 401 */       p *= curveLength;
/*     */       while (true) {
/* 403 */         float length = segments[segment];
/* 404 */         if (p > length) { segment++; continue; }
/* 405 */          if (segment == 0) {
/* 406 */           p /= length; break;
/*     */         } 
/* 408 */         float prev = segments[segment - 1];
/* 409 */         p = segment + (p - prev) / (length - prev);
/*     */         
/*     */         break;
/*     */       } 
/* 413 */       addCurvePosition(p * 0.1F, x1, y1, cx1, cy1, cx2, cy2, x2, y2, out, o, (tangents || (j > 0 && space < 1.0E-5F))); continue;
/*     */     } 
/* 415 */     return out;
/*     */   }
/*     */   
/*     */   private void addBeforePosition(float p, float[] temp, int i, float[] out, int o) {
/* 419 */     float x1 = temp[i], y1 = temp[i + 1], dx = temp[i + 2] - x1, dy = temp[i + 3] - y1, r = (float)Math.atan2(dy, dx);
/* 420 */     out[o] = x1 + p * (float)Math.cos(r);
/* 421 */     out[o + 1] = y1 + p * (float)Math.sin(r);
/* 422 */     out[o + 2] = r;
/*     */   }
/*     */   
/*     */   private void addAfterPosition(float p, float[] temp, int i, float[] out, int o) {
/* 426 */     float x1 = temp[i + 2], y1 = temp[i + 3], dx = x1 - temp[i], dy = y1 - temp[i + 1], r = (float)Math.atan2(dy, dx);
/* 427 */     out[o] = x1 + p * (float)Math.cos(r);
/* 428 */     out[o + 1] = y1 + p * (float)Math.sin(r);
/* 429 */     out[o + 2] = r;
/*     */   }
/*     */ 
/*     */   
/*     */   private void addCurvePosition(float p, float x1, float y1, float cx1, float cy1, float cx2, float cy2, float x2, float y2, float[] out, int o, boolean tangents) {
/* 434 */     if (p < 1.0E-5F || Float.isNaN(p)) {
/* 435 */       out[o] = x1;
/* 436 */       out[o + 1] = y1;
/* 437 */       out[o + 2] = (float)Math.atan2((cy1 - y1), (cx1 - x1));
/*     */       return;
/*     */     } 
/* 440 */     float tt = p * p, ttt = tt * p, u = 1.0F - p, uu = u * u, uuu = uu * u;
/* 441 */     float ut = u * p, ut3 = ut * 3.0F, uut3 = u * ut3, utt3 = ut3 * p;
/* 442 */     float x = x1 * uuu + cx1 * uut3 + cx2 * utt3 + x2 * ttt, y = y1 * uuu + cy1 * uut3 + cy2 * utt3 + y2 * ttt;
/* 443 */     out[o] = x;
/* 444 */     out[o + 1] = y;
/* 445 */     if (tangents) {
/* 446 */       if (p < 0.001F) {
/* 447 */         out[o + 2] = (float)Math.atan2((cy1 - y1), (cx1 - x1));
/*     */       } else {
/* 449 */         out[o + 2] = (float)Math.atan2((y - y1 * uu + cy1 * ut * 2.0F + cy2 * tt), (x - x1 * uu + cx1 * ut * 2.0F + cx2 * tt));
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public float getPosition() {
/* 455 */     return this.position;
/*     */   }
/*     */   
/*     */   public void setPosition(float position) {
/* 459 */     this.position = position;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getSpacing() {
/* 464 */     return this.spacing;
/*     */   }
/*     */   
/*     */   public void setSpacing(float spacing) {
/* 468 */     this.spacing = spacing;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getRotateMix() {
/* 473 */     return this.rotateMix;
/*     */   }
/*     */   
/*     */   public void setRotateMix(float rotateMix) {
/* 477 */     this.rotateMix = rotateMix;
/*     */   }
/*     */ 
/*     */   
/*     */   public float getTranslateMix() {
/* 482 */     return this.translateMix;
/*     */   }
/*     */   
/*     */   public void setTranslateMix(float translateMix) {
/* 486 */     this.translateMix = translateMix;
/*     */   }
/*     */ 
/*     */   
/*     */   public Array<Bone> getBones() {
/* 491 */     return this.bones;
/*     */   }
/*     */ 
/*     */   
/*     */   public Slot getTarget() {
/* 496 */     return this.target;
/*     */   }
/*     */   
/*     */   public void setTarget(Slot target) {
/* 500 */     if (target == null) throw new IllegalArgumentException("target cannot be null."); 
/* 501 */     this.target = target;
/*     */   }
/*     */   
/*     */   public boolean isActive() {
/* 505 */     return this.active;
/*     */   }
/*     */ 
/*     */   
/*     */   public PathConstraintData getData() {
/* 510 */     return this.data;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 514 */     return this.data.name;
/*     */   }
/*     */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\PathConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */