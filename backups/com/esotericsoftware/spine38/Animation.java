/*      */ package com.esotericsoftware.spine38;
/*      */ 
/*      */ import com.badlogic.gdx.graphics.Color;
/*      */ import com.badlogic.gdx.math.MathUtils;
/*      */ import com.badlogic.gdx.utils.Array;
/*      */ import com.badlogic.gdx.utils.FloatArray;
/*      */ import com.badlogic.gdx.utils.IntSet;
/*      */ import com.esotericsoftware.spine38.attachments.Attachment;
/*      */ import com.esotericsoftware.spine38.attachments.VertexAttachment;
/*      */ import com.esotericsoftware.spine38.utils.SpineUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Animation
/*      */ {
/*      */   final String name;
/*      */   Array<Timeline> timelines;
/*   49 */   final IntSet timelineIDs = new IntSet();
/*      */   float duration;
/*      */   
/*      */   public Animation(String name, Array<Timeline> timelines, float duration) {
/*   53 */     if (name == null) throw new IllegalArgumentException("name cannot be null."); 
/*   54 */     this.name = name;
/*   55 */     this.duration = duration;
/*   56 */     setTimelines(timelines);
/*      */   }
/*      */ 
/*      */   
/*      */   public Array<Timeline> getTimelines() {
/*   61 */     return this.timelines;
/*      */   }
/*      */   
/*      */   public void setTimelines(Array<Timeline> timelines) {
/*   65 */     if (timelines == null) throw new IllegalArgumentException("timelines cannot be null."); 
/*   66 */     this.timelines = timelines;
/*      */     
/*   68 */     this.timelineIDs.clear();
/*   69 */     for (Timeline timeline : timelines) {
/*   70 */       this.timelineIDs.add(timeline.getPropertyId());
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean hasTimeline(int id) {
/*   75 */     return this.timelineIDs.contains(id);
/*      */   }
/*      */ 
/*      */   
/*      */   public float getDuration() {
/*   80 */     return this.duration;
/*      */   }
/*      */   
/*      */   public void setDuration(float duration) {
/*   84 */     this.duration = duration;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void apply(Skeleton skeleton, float lastTime, float time, boolean loop, Array<Event> events, float alpha, MixBlend blend, MixDirection direction) {
/*   94 */     if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null.");
/*      */     
/*   96 */     if (loop && this.duration != 0.0F) {
/*   97 */       time %= this.duration;
/*   98 */       if (lastTime > 0.0F) lastTime %= this.duration;
/*      */     
/*      */     } 
/*  101 */     Array<Timeline> timelines = this.timelines;
/*  102 */     for (int i = 0, n = timelines.size; i < n; i++) {
/*  103 */       ((Timeline)timelines.get(i)).apply(skeleton, lastTime, time, events, alpha, blend, direction);
/*      */     }
/*      */   }
/*      */   
/*      */   public String getName() {
/*  108 */     return this.name;
/*      */   }
/*      */   
/*      */   public String toString() {
/*  112 */     return this.name;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int binarySearch(float[] values, float target, int step) {
/*  118 */     int low = 0;
/*  119 */     int high = values.length / step - 2;
/*  120 */     if (high == 0) return step; 
/*  121 */     int current = high >>> 1;
/*      */     while (true) {
/*  123 */       if (values[(current + 1) * step] <= target) {
/*  124 */         low = current + 1;
/*      */       } else {
/*  126 */         high = current;
/*  127 */       }  if (low == high) return (low + 1) * step; 
/*  128 */       current = low + high >>> 1;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static int binarySearch(float[] values, float target) {
/*  135 */     int low = 0;
/*  136 */     int high = values.length - 2;
/*  137 */     if (high == 0) return 1; 
/*  138 */     int current = high >>> 1;
/*      */     while (true) {
/*  140 */       if (values[current + 1] <= target) {
/*  141 */         low = current + 1;
/*      */       } else {
/*  143 */         high = current;
/*  144 */       }  if (low == high) return low + 1; 
/*  145 */       current = low + high >>> 1;
/*      */     } 
/*      */   }
/*      */   
/*      */   static int linearSearch(float[] values, float target, int step) {
/*  150 */     for (int i = 0, last = values.length - step; i <= last; i += step) {
/*  151 */       if (values[i] > target) return i; 
/*  152 */     }  return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public enum MixBlend
/*      */   {
/*  188 */     setup,
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  194 */     first,
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  199 */     replace,
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  206 */     add;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public enum MixDirection
/*      */   {
/*  214 */     in, out;
/*      */   }
/*      */   
/*      */   private enum TimelineType {
/*  218 */     rotate, translate, scale, shear,
/*  219 */     attachment, color, deform,
/*  220 */     event, drawOrder,
/*  221 */     ikConstraint, transformConstraint,
/*  222 */     pathConstraintPosition, pathConstraintSpacing, pathConstraintMix,
/*  223 */     twoColor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static abstract class CurveTimeline
/*      */     implements Timeline
/*      */   {
/*      */     public static final float LINEAR = 0.0F;
/*      */ 
/*      */ 
/*      */     
/*      */     public static final float STEPPED = 1.0F;
/*      */ 
/*      */     
/*      */     public static final float BEZIER = 2.0F;
/*      */ 
/*      */     
/*      */     private static final int BEZIER_SIZE = 19;
/*      */ 
/*      */     
/*      */     private final float[] curves;
/*      */ 
/*      */ 
/*      */     
/*      */     public CurveTimeline(int frameCount) {
/*  250 */       if (frameCount <= 0) throw new IllegalArgumentException("frameCount must be > 0: " + frameCount); 
/*  251 */       this.curves = new float[(frameCount - 1) * 19];
/*      */     }
/*      */ 
/*      */     
/*      */     public int getFrameCount() {
/*  256 */       return this.curves.length / 19 + 1;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setLinear(int frameIndex) {
/*  261 */       this.curves[frameIndex * 19] = 0.0F;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setStepped(int frameIndex) {
/*  266 */       this.curves[frameIndex * 19] = 1.0F;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public float getCurveType(int frameIndex) {
/*  272 */       int index = frameIndex * 19;
/*  273 */       if (index == this.curves.length) return 0.0F; 
/*  274 */       float type = this.curves[index];
/*  275 */       if (type == 0.0F) return 0.0F; 
/*  276 */       if (type == 1.0F) return 1.0F; 
/*  277 */       return 2.0F;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setCurve(int frameIndex, float cx1, float cy1, float cx2, float cy2) {
/*  284 */       float tmpx = (-cx1 * 2.0F + cx2) * 0.03F, tmpy = (-cy1 * 2.0F + cy2) * 0.03F;
/*  285 */       float dddfx = ((cx1 - cx2) * 3.0F + 1.0F) * 0.006F, dddfy = ((cy1 - cy2) * 3.0F + 1.0F) * 0.006F;
/*  286 */       float ddfx = tmpx * 2.0F + dddfx, ddfy = tmpy * 2.0F + dddfy;
/*  287 */       float dfx = cx1 * 0.3F + tmpx + dddfx * 0.16666667F, dfy = cy1 * 0.3F + tmpy + dddfy * 0.16666667F;
/*      */       
/*  289 */       int i = frameIndex * 19;
/*  290 */       float[] curves = this.curves;
/*  291 */       curves[i++] = 2.0F;
/*      */       
/*  293 */       float x = dfx, y = dfy;
/*  294 */       for (int n = i + 19 - 1; i < n; i += 2) {
/*  295 */         curves[i] = x;
/*  296 */         curves[i + 1] = y;
/*  297 */         dfx += ddfx;
/*  298 */         dfy += ddfy;
/*  299 */         ddfx += dddfx;
/*  300 */         ddfy += dddfy;
/*  301 */         x += dfx;
/*  302 */         y += dfy;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public float getCurvePercent(int frameIndex, float percent) {
/*  308 */       percent = MathUtils.clamp(percent, 0.0F, 1.0F);
/*  309 */       float[] curves = this.curves;
/*  310 */       int i = frameIndex * 19;
/*  311 */       float type = curves[i];
/*  312 */       if (type == 0.0F) return percent; 
/*  313 */       if (type == 1.0F) return 0.0F; 
/*  314 */       i++;
/*  315 */       float x = 0.0F;
/*  316 */       for (int start = i, n = i + 19 - 1; i < n; i += 2) {
/*  317 */         x = curves[i];
/*  318 */         if (x >= percent) {
/*  319 */           if (i == start) return curves[i + 1] * percent / x; 
/*  320 */           float prevX = curves[i - 2], prevY = curves[i - 1];
/*  321 */           return prevY + (curves[i + 1] - prevY) * (percent - prevX) / (x - prevX);
/*      */         } 
/*      */       } 
/*  324 */       float y = curves[i - 1];
/*  325 */       return y + (1.0F - y) * (percent - x) / (1.0F - x);
/*      */     }
/*      */   }
/*      */   
/*      */   public static class RotateTimeline
/*      */     extends CurveTimeline implements BoneTimeline {
/*      */     public static final int ENTRIES = 2;
/*      */     static final int PREV_TIME = -2;
/*      */     static final int PREV_ROTATION = -1;
/*      */     static final int ROTATION = 1;
/*      */     int boneIndex;
/*      */     final float[] frames;
/*      */     
/*      */     public RotateTimeline(int frameCount) {
/*  339 */       super(frameCount);
/*  340 */       this.frames = new float[frameCount << 1];
/*      */     }
/*      */     
/*      */     public int getPropertyId() {
/*  344 */       return (Animation.TimelineType.rotate.ordinal() << 24) + this.boneIndex;
/*      */     }
/*      */     
/*      */     public void setBoneIndex(int index) {
/*  348 */       if (index < 0) throw new IllegalArgumentException("index must be >= 0."); 
/*  349 */       this.boneIndex = index;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getBoneIndex() {
/*  354 */       return this.boneIndex;
/*      */     }
/*      */ 
/*      */     
/*      */     public float[] getFrames() {
/*  359 */       return this.frames;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setFrame(int frameIndex, float time, float degrees) {
/*  364 */       frameIndex <<= 1;
/*  365 */       this.frames[frameIndex] = time;
/*  366 */       this.frames[frameIndex + 1] = degrees;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> events, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/*  372 */       Bone bone = (Bone)skeleton.bones.get(this.boneIndex);
/*  373 */       if (!bone.active)
/*  374 */         return;  float[] frames = this.frames;
/*  375 */       if (time < frames[0]) {
/*  376 */         float f; switch (blend) {
/*      */           case setup:
/*  378 */             bone.rotation = bone.data.rotation;
/*      */             return;
/*      */           case first:
/*  381 */             f = bone.data.rotation - bone.rotation;
/*  382 */             bone.rotation += (f - ((16384 - (int)(16384.499999999996D - (f / 360.0F))) * 360)) * alpha;
/*      */             break;
/*      */         } 
/*      */         return;
/*      */       } 
/*  387 */       if (time >= frames[frames.length - 2]) {
/*  388 */         float f = frames[frames.length + -1];
/*  389 */         switch (blend) {
/*      */           case setup:
/*  391 */             bone.rotation = bone.data.rotation + f * alpha;
/*      */             break;
/*      */           case first:
/*      */           case replace:
/*  395 */             f += bone.data.rotation - bone.rotation;
/*  396 */             f -= ((16384 - (int)(16384.499999999996D - (f / 360.0F))) * 360);
/*      */           
/*      */           case add:
/*  399 */             bone.rotation += f * alpha;
/*      */             break;
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/*  405 */       int frame = Animation.binarySearch(frames, time, 2);
/*  406 */       float prevRotation = frames[frame + -1];
/*  407 */       float frameTime = frames[frame];
/*  408 */       float percent = getCurvePercent((frame >> 1) - 1, 1.0F - (time - frameTime) / (frames[frame + -2] - frameTime));
/*      */       
/*  410 */       float r = frames[frame + 1] - prevRotation;
/*  411 */       r = prevRotation + (r - ((16384 - (int)(16384.499999999996D - (r / 360.0F))) * 360)) * percent;
/*  412 */       switch (blend) {
/*      */         case setup:
/*  414 */           bone.rotation = bone.data.rotation + (r - ((16384 - (int)(16384.499999999996D - (r / 360.0F))) * 360)) * alpha;
/*      */           break;
/*      */         case first:
/*      */         case replace:
/*  418 */           r += bone.data.rotation - bone.rotation;
/*      */         
/*      */         case add:
/*  421 */           bone.rotation += (r - ((16384 - (int)(16384.499999999996D - (r / 360.0F))) * 360)) * alpha;
/*      */           break;
/*      */       } 
/*      */     } }
/*      */   
/*      */   public static class TranslateTimeline extends CurveTimeline implements BoneTimeline { public static final int ENTRIES = 3;
/*      */     static final int PREV_TIME = -3;
/*      */     static final int PREV_X = -2;
/*      */     static final int PREV_Y = -1;
/*      */     static final int X = 1;
/*      */     static final int Y = 2;
/*      */     int boneIndex;
/*      */     final float[] frames;
/*      */     
/*      */     public TranslateTimeline(int frameCount) {
/*  436 */       super(frameCount);
/*  437 */       this.frames = new float[frameCount * 3];
/*      */     }
/*      */     
/*      */     public int getPropertyId() {
/*  441 */       return (Animation.TimelineType.translate.ordinal() << 24) + this.boneIndex;
/*      */     }
/*      */     
/*      */     public void setBoneIndex(int index) {
/*  445 */       if (index < 0) throw new IllegalArgumentException("index must be >= 0."); 
/*  446 */       this.boneIndex = index;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getBoneIndex() {
/*  451 */       return this.boneIndex;
/*      */     }
/*      */ 
/*      */     
/*      */     public float[] getFrames() {
/*  456 */       return this.frames;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setFrame(int frameIndex, float time, float x, float y) {
/*  461 */       frameIndex *= 3;
/*  462 */       this.frames[frameIndex] = time;
/*  463 */       this.frames[frameIndex + 1] = x;
/*  464 */       this.frames[frameIndex + 2] = y;
/*      */     }
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> events, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/*      */       float x, y;
/*  470 */       Bone bone = (Bone)skeleton.bones.get(this.boneIndex);
/*  471 */       if (!bone.active)
/*  472 */         return;  float[] frames = this.frames;
/*  473 */       if (time < frames[0]) {
/*  474 */         switch (blend) {
/*      */           case setup:
/*  476 */             bone.x = bone.data.x;
/*  477 */             bone.y = bone.data.y;
/*      */             return;
/*      */           case first:
/*  480 */             bone.x += (bone.data.x - bone.x) * alpha;
/*  481 */             bone.y += (bone.data.y - bone.y) * alpha;
/*      */             break;
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/*  487 */       if (time >= frames[frames.length - 3]) {
/*  488 */         x = frames[frames.length + -2];
/*  489 */         y = frames[frames.length + -1];
/*      */       } else {
/*      */         
/*  492 */         int frame = Animation.binarySearch(frames, time, 3);
/*  493 */         x = frames[frame + -2];
/*  494 */         y = frames[frame + -1];
/*  495 */         float frameTime = frames[frame];
/*  496 */         float percent = getCurvePercent(frame / 3 - 1, 1.0F - (time - frameTime) / (frames[frame + -3] - frameTime));
/*      */ 
/*      */         
/*  499 */         x += (frames[frame + 1] - x) * percent;
/*  500 */         y += (frames[frame + 2] - y) * percent;
/*      */       } 
/*  502 */       switch (blend) {
/*      */         case setup:
/*  504 */           bone.x = bone.data.x + x * alpha;
/*  505 */           bone.y = bone.data.y + y * alpha;
/*      */           break;
/*      */         case first:
/*      */         case replace:
/*  509 */           bone.x += (bone.data.x + x - bone.x) * alpha;
/*  510 */           bone.y += (bone.data.y + y - bone.y) * alpha;
/*      */           break;
/*      */         case add:
/*  513 */           bone.x += x * alpha;
/*  514 */           bone.y += y * alpha;
/*      */           break;
/*      */       } 
/*      */     } }
/*      */ 
/*      */   
/*      */   public static class ScaleTimeline extends TranslateTimeline {
/*      */     public ScaleTimeline(int frameCount) {
/*  522 */       super(frameCount);
/*      */     }
/*      */     
/*      */     public int getPropertyId() {
/*  526 */       return (Animation.TimelineType.scale.ordinal() << 24) + this.boneIndex;
/*      */     }
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> events, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/*      */       float x, y;
/*  532 */       Bone bone = (Bone)skeleton.bones.get(this.boneIndex);
/*  533 */       if (!bone.active)
/*  534 */         return;  float[] frames = this.frames;
/*  535 */       if (time < frames[0]) {
/*  536 */         switch (blend) {
/*      */           case setup:
/*  538 */             bone.scaleX = bone.data.scaleX;
/*  539 */             bone.scaleY = bone.data.scaleY;
/*      */             return;
/*      */           case first:
/*  542 */             bone.scaleX += (bone.data.scaleX - bone.scaleX) * alpha;
/*  543 */             bone.scaleY += (bone.data.scaleY - bone.scaleY) * alpha;
/*      */             break;
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/*  549 */       if (time >= frames[frames.length - 3]) {
/*  550 */         x = frames[frames.length + -2] * bone.data.scaleX;
/*  551 */         y = frames[frames.length + -1] * bone.data.scaleY;
/*      */       } else {
/*      */         
/*  554 */         int frame = Animation.binarySearch(frames, time, 3);
/*  555 */         x = frames[frame + -2];
/*  556 */         y = frames[frame + -1];
/*  557 */         float frameTime = frames[frame];
/*  558 */         float percent = getCurvePercent(frame / 3 - 1, 1.0F - (time - frameTime) / (frames[frame + -3] - frameTime));
/*      */ 
/*      */         
/*  561 */         x = (x + (frames[frame + 1] - x) * percent) * bone.data.scaleX;
/*  562 */         y = (y + (frames[frame + 2] - y) * percent) * bone.data.scaleY;
/*      */       } 
/*  564 */       if (alpha == 1.0F) {
/*  565 */         if (blend == Animation.MixBlend.add) {
/*  566 */           bone.scaleX += x - bone.data.scaleX;
/*  567 */           bone.scaleY += y - bone.data.scaleY;
/*      */         } else {
/*  569 */           bone.scaleX = x;
/*  570 */           bone.scaleY = y;
/*      */         
/*      */         }
/*      */       
/*      */       }
/*  575 */       else if (direction == Animation.MixDirection.out) {
/*  576 */         float bx; float by; switch (blend) {
/*      */           case setup:
/*  578 */             bx = bone.data.scaleX;
/*  579 */             by = bone.data.scaleY;
/*  580 */             bone.scaleX = bx + (Math.abs(x) * Math.signum(bx) - bx) * alpha;
/*  581 */             bone.scaleY = by + (Math.abs(y) * Math.signum(by) - by) * alpha;
/*      */             break;
/*      */           case first:
/*      */           case replace:
/*  585 */             bx = bone.scaleX;
/*  586 */             by = bone.scaleY;
/*  587 */             bone.scaleX = bx + (Math.abs(x) * Math.signum(bx) - bx) * alpha;
/*  588 */             bone.scaleY = by + (Math.abs(y) * Math.signum(by) - by) * alpha;
/*      */             break;
/*      */           case add:
/*  591 */             bx = bone.scaleX;
/*  592 */             by = bone.scaleY;
/*  593 */             bone.scaleX = bx + (Math.abs(x) * Math.signum(bx) - bone.data.scaleX) * alpha;
/*  594 */             bone.scaleY = by + (Math.abs(y) * Math.signum(by) - bone.data.scaleY) * alpha; break;
/*      */         } 
/*      */       } else {
/*  597 */         float bx; float by; switch (blend) {
/*      */           case setup:
/*  599 */             bx = Math.abs(bone.data.scaleX) * Math.signum(x);
/*  600 */             by = Math.abs(bone.data.scaleY) * Math.signum(y);
/*  601 */             bone.scaleX = bx + (x - bx) * alpha;
/*  602 */             bone.scaleY = by + (y - by) * alpha;
/*      */             break;
/*      */           case first:
/*      */           case replace:
/*  606 */             bx = Math.abs(bone.scaleX) * Math.signum(x);
/*  607 */             by = Math.abs(bone.scaleY) * Math.signum(y);
/*  608 */             bone.scaleX = bx + (x - bx) * alpha;
/*  609 */             bone.scaleY = by + (y - by) * alpha;
/*      */             break;
/*      */           case add:
/*  612 */             bx = Math.signum(x);
/*  613 */             by = Math.signum(y);
/*  614 */             bone.scaleX = Math.abs(bone.scaleX) * bx + (x - Math.abs(bone.data.scaleX) * bx) * alpha;
/*  615 */             bone.scaleY = Math.abs(bone.scaleY) * by + (y - Math.abs(bone.data.scaleY) * by) * alpha;
/*      */             break;
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   public static class ShearTimeline
/*      */     extends TranslateTimeline {
/*      */     public ShearTimeline(int frameCount) {
/*  625 */       super(frameCount);
/*      */     }
/*      */     
/*      */     public int getPropertyId() {
/*  629 */       return (Animation.TimelineType.shear.ordinal() << 24) + this.boneIndex;
/*      */     }
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> events, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/*      */       float x, y;
/*  635 */       Bone bone = (Bone)skeleton.bones.get(this.boneIndex);
/*  636 */       if (!bone.active)
/*  637 */         return;  float[] frames = this.frames;
/*  638 */       if (time < frames[0]) {
/*  639 */         switch (blend) {
/*      */           case setup:
/*  641 */             bone.shearX = bone.data.shearX;
/*  642 */             bone.shearY = bone.data.shearY;
/*      */             return;
/*      */           case first:
/*  645 */             bone.shearX += (bone.data.shearX - bone.shearX) * alpha;
/*  646 */             bone.shearY += (bone.data.shearY - bone.shearY) * alpha;
/*      */             break;
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/*  652 */       if (time >= frames[frames.length - 3]) {
/*  653 */         x = frames[frames.length + -2];
/*  654 */         y = frames[frames.length + -1];
/*      */       } else {
/*      */         
/*  657 */         int frame = Animation.binarySearch(frames, time, 3);
/*  658 */         x = frames[frame + -2];
/*  659 */         y = frames[frame + -1];
/*  660 */         float frameTime = frames[frame];
/*  661 */         float percent = getCurvePercent(frame / 3 - 1, 1.0F - (time - frameTime) / (frames[frame + -3] - frameTime));
/*      */ 
/*      */         
/*  664 */         x += (frames[frame + 1] - x) * percent;
/*  665 */         y += (frames[frame + 2] - y) * percent;
/*      */       } 
/*  667 */       switch (blend) {
/*      */         case setup:
/*  669 */           bone.shearX = bone.data.shearX + x * alpha;
/*  670 */           bone.shearY = bone.data.shearY + y * alpha;
/*      */           break;
/*      */         case first:
/*      */         case replace:
/*  674 */           bone.shearX += (bone.data.shearX + x - bone.shearX) * alpha;
/*  675 */           bone.shearY += (bone.data.shearY + y - bone.shearY) * alpha;
/*      */           break;
/*      */         case add:
/*  678 */           bone.shearX += x * alpha;
/*  679 */           bone.shearY += y * alpha;
/*      */           break;
/*      */       } 
/*      */     } }
/*      */   public static class ColorTimeline extends CurveTimeline implements SlotTimeline { public static final int ENTRIES = 5; private static final int PREV_TIME = -5; private static final int PREV_R = -4; private static final int PREV_G = -3;
/*      */     private static final int PREV_B = -2;
/*      */     private static final int PREV_A = -1;
/*      */     private static final int R = 1;
/*      */     private static final int G = 2;
/*      */     private static final int B = 3;
/*      */     private static final int A = 4;
/*      */     int slotIndex;
/*      */     private final float[] frames;
/*      */     
/*      */     public ColorTimeline(int frameCount) {
/*  694 */       super(frameCount);
/*  695 */       this.frames = new float[frameCount * 5];
/*      */     }
/*      */     
/*      */     public int getPropertyId() {
/*  699 */       return (Animation.TimelineType.color.ordinal() << 24) + this.slotIndex;
/*      */     }
/*      */     
/*      */     public void setSlotIndex(int index) {
/*  703 */       if (index < 0) throw new IllegalArgumentException("index must be >= 0."); 
/*  704 */       this.slotIndex = index;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSlotIndex() {
/*  709 */       return this.slotIndex;
/*      */     }
/*      */ 
/*      */     
/*      */     public float[] getFrames() {
/*  714 */       return this.frames;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setFrame(int frameIndex, float time, float r, float g, float b, float a) {
/*  719 */       frameIndex *= 5;
/*  720 */       this.frames[frameIndex] = time;
/*  721 */       this.frames[frameIndex + 1] = r;
/*  722 */       this.frames[frameIndex + 2] = g;
/*  723 */       this.frames[frameIndex + 3] = b;
/*  724 */       this.frames[frameIndex + 4] = a;
/*      */     }
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> events, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/*      */       float r, g, b, a;
/*  730 */       Slot slot = (Slot)skeleton.slots.get(this.slotIndex);
/*  731 */       if (!slot.bone.active)
/*  732 */         return;  float[] frames = this.frames;
/*  733 */       if (time < frames[0]) {
/*  734 */         Color color; Color setup; switch (blend) {
/*      */           case setup:
/*  736 */             slot.color.set(slot.data.color);
/*      */             return;
/*      */           case first:
/*  739 */             color = slot.color; setup = slot.data.color;
/*  740 */             color.add((setup.r - color.r) * alpha, (setup.g - color.g) * alpha, (setup.b - color.b) * alpha, (setup.a - color.a) * alpha);
/*      */             break;
/*      */         } 
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*  747 */       if (time >= frames[frames.length - 5]) {
/*  748 */         int i = frames.length;
/*  749 */         r = frames[i + -4];
/*  750 */         g = frames[i + -3];
/*  751 */         b = frames[i + -2];
/*  752 */         a = frames[i + -1];
/*      */       } else {
/*      */         
/*  755 */         int frame = Animation.binarySearch(frames, time, 5);
/*  756 */         r = frames[frame + -4];
/*  757 */         g = frames[frame + -3];
/*  758 */         b = frames[frame + -2];
/*  759 */         a = frames[frame + -1];
/*  760 */         float frameTime = frames[frame];
/*  761 */         float percent = getCurvePercent(frame / 5 - 1, 1.0F - (time - frameTime) / (frames[frame + -5] - frameTime));
/*      */ 
/*      */         
/*  764 */         r += (frames[frame + 1] - r) * percent;
/*  765 */         g += (frames[frame + 2] - g) * percent;
/*  766 */         b += (frames[frame + 3] - b) * percent;
/*  767 */         a += (frames[frame + 4] - a) * percent;
/*      */       } 
/*  769 */       if (alpha == 1.0F) {
/*  770 */         slot.color.set(r, g, b, a);
/*      */       } else {
/*  772 */         Color color = slot.color;
/*  773 */         if (blend == Animation.MixBlend.setup) color.set(slot.data.color); 
/*  774 */         color.add((r - color.r) * alpha, (g - color.g) * alpha, (b - color.b) * alpha, (a - color.a) * alpha);
/*      */       } 
/*      */     } }
/*      */   
/*      */   public static class TwoColorTimeline extends CurveTimeline implements SlotTimeline {
/*      */     public static final int ENTRIES = 8;
/*      */     private static final int PREV_TIME = -8;
/*      */     private static final int PREV_R = -7;
/*      */     private static final int PREV_G = -6;
/*      */     private static final int PREV_B = -5;
/*      */     private static final int PREV_A = -4;
/*      */     private static final int PREV_R2 = -3;
/*      */     private static final int PREV_G2 = -2;
/*      */     private static final int PREV_B2 = -1;
/*      */     
/*      */     public TwoColorTimeline(int frameCount) {
/*  790 */       super(frameCount);
/*  791 */       this.frames = new float[frameCount * 8];
/*      */     }
/*      */     private static final int R = 1; private static final int G = 2; private static final int B = 3; private static final int A = 4; private static final int R2 = 5; private static final int G2 = 6; private static final int B2 = 7; int slotIndex; private final float[] frames;
/*      */     public int getPropertyId() {
/*  795 */       return (Animation.TimelineType.twoColor.ordinal() << 24) + this.slotIndex;
/*      */     }
/*      */     
/*      */     public void setSlotIndex(int index) {
/*  799 */       if (index < 0) throw new IllegalArgumentException("index must be >= 0."); 
/*  800 */       this.slotIndex = index;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int getSlotIndex() {
/*  806 */       return this.slotIndex;
/*      */     }
/*      */ 
/*      */     
/*      */     public float[] getFrames() {
/*  811 */       return this.frames;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setFrame(int frameIndex, float time, float r, float g, float b, float a, float r2, float g2, float b2) {
/*  816 */       frameIndex *= 8;
/*  817 */       this.frames[frameIndex] = time;
/*  818 */       this.frames[frameIndex + 1] = r;
/*  819 */       this.frames[frameIndex + 2] = g;
/*  820 */       this.frames[frameIndex + 3] = b;
/*  821 */       this.frames[frameIndex + 4] = a;
/*  822 */       this.frames[frameIndex + 5] = r2;
/*  823 */       this.frames[frameIndex + 6] = g2;
/*  824 */       this.frames[frameIndex + 7] = b2;
/*      */     }
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> events, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/*      */       float r, g, b, a, r2, g2, b2;
/*  830 */       Slot slot = (Slot)skeleton.slots.get(this.slotIndex);
/*  831 */       if (!slot.bone.active)
/*  832 */         return;  float[] frames = this.frames;
/*  833 */       if (time < frames[0]) {
/*  834 */         Color light; Color dark; Color setupLight; Color setupDark; switch (blend) {
/*      */           case setup:
/*  836 */             slot.color.set(slot.data.color);
/*  837 */             slot.darkColor.set(slot.data.darkColor);
/*      */             return;
/*      */           case first:
/*  840 */             light = slot.color; dark = slot.darkColor; setupLight = slot.data.color; setupDark = slot.data.darkColor;
/*  841 */             light.add((setupLight.r - light.r) * alpha, (setupLight.g - light.g) * alpha, (setupLight.b - light.b) * alpha, (setupLight.a - light.a) * alpha);
/*      */             
/*  843 */             dark.add((setupDark.r - dark.r) * alpha, (setupDark.g - dark.g) * alpha, (setupDark.b - dark.b) * alpha, 0.0F);
/*      */             break;
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/*  849 */       if (time >= frames[frames.length - 8]) {
/*  850 */         int i = frames.length;
/*  851 */         r = frames[i + -7];
/*  852 */         g = frames[i + -6];
/*  853 */         b = frames[i + -5];
/*  854 */         a = frames[i + -4];
/*  855 */         r2 = frames[i + -3];
/*  856 */         g2 = frames[i + -2];
/*  857 */         b2 = frames[i + -1];
/*      */       } else {
/*      */         
/*  860 */         int frame = Animation.binarySearch(frames, time, 8);
/*  861 */         r = frames[frame + -7];
/*  862 */         g = frames[frame + -6];
/*  863 */         b = frames[frame + -5];
/*  864 */         a = frames[frame + -4];
/*  865 */         r2 = frames[frame + -3];
/*  866 */         g2 = frames[frame + -2];
/*  867 */         b2 = frames[frame + -1];
/*  868 */         float frameTime = frames[frame];
/*  869 */         float percent = getCurvePercent(frame / 8 - 1, 1.0F - (time - frameTime) / (frames[frame + -8] - frameTime));
/*      */ 
/*      */         
/*  872 */         r += (frames[frame + 1] - r) * percent;
/*  873 */         g += (frames[frame + 2] - g) * percent;
/*  874 */         b += (frames[frame + 3] - b) * percent;
/*  875 */         a += (frames[frame + 4] - a) * percent;
/*  876 */         r2 += (frames[frame + 5] - r2) * percent;
/*  877 */         g2 += (frames[frame + 6] - g2) * percent;
/*  878 */         b2 += (frames[frame + 7] - b2) * percent;
/*      */       } 
/*  880 */       if (alpha == 1.0F) {
/*  881 */         slot.color.set(r, g, b, a);
/*  882 */         slot.darkColor.set(r2, g2, b2, 1.0F);
/*      */       } else {
/*  884 */         Color light = slot.color, dark = slot.darkColor;
/*  885 */         if (blend == Animation.MixBlend.setup) {
/*  886 */           light.set(slot.data.color);
/*  887 */           dark.set(slot.data.darkColor);
/*      */         } 
/*  889 */         light.add((r - light.r) * alpha, (g - light.g) * alpha, (b - light.b) * alpha, (a - light.a) * alpha);
/*  890 */         dark.add((r2 - dark.r) * alpha, (g2 - dark.g) * alpha, (b2 - dark.b) * alpha, 0.0F);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   public static class AttachmentTimeline
/*      */     implements SlotTimeline {
/*      */     int slotIndex;
/*      */     final float[] frames;
/*      */     final String[] attachmentNames;
/*      */     
/*      */     public AttachmentTimeline(int frameCount) {
/*  902 */       if (frameCount <= 0) throw new IllegalArgumentException("frameCount must be > 0: " + frameCount); 
/*  903 */       this.frames = new float[frameCount];
/*  904 */       this.attachmentNames = new String[frameCount];
/*      */     }
/*      */     
/*      */     public int getPropertyId() {
/*  908 */       return (Animation.TimelineType.attachment.ordinal() << 24) + this.slotIndex;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getFrameCount() {
/*  913 */       return this.frames.length;
/*      */     }
/*      */     
/*      */     public void setSlotIndex(int index) {
/*  917 */       if (index < 0) throw new IllegalArgumentException("index must be >= 0."); 
/*  918 */       this.slotIndex = index;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSlotIndex() {
/*  923 */       return this.slotIndex;
/*      */     }
/*      */ 
/*      */     
/*      */     public float[] getFrames() {
/*  928 */       return this.frames;
/*      */     }
/*      */ 
/*      */     
/*      */     public String[] getAttachmentNames() {
/*  933 */       return this.attachmentNames;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setFrame(int frameIndex, float time, String attachmentName) {
/*  938 */       this.frames[frameIndex] = time;
/*  939 */       this.attachmentNames[frameIndex] = attachmentName;
/*      */     }
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> events, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/*      */       int frameIndex;
/*  945 */       Slot slot = (Slot)skeleton.slots.get(this.slotIndex);
/*  946 */       if (!slot.bone.active)
/*  947 */         return;  if (direction == Animation.MixDirection.out) {
/*  948 */         if (blend == Animation.MixBlend.setup) setAttachment(skeleton, slot, slot.data.attachmentName);
/*      */         
/*      */         return;
/*      */       } 
/*  952 */       float[] frames = this.frames;
/*  953 */       if (time < frames[0]) {
/*  954 */         if (blend == Animation.MixBlend.setup || blend == Animation.MixBlend.first) setAttachment(skeleton, slot, slot.data.attachmentName);
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*  959 */       if (time >= frames[frames.length - 1]) {
/*  960 */         frameIndex = frames.length - 1;
/*      */       } else {
/*  962 */         frameIndex = Animation.binarySearch(frames, time) - 1;
/*      */       } 
/*  964 */       setAttachment(skeleton, slot, this.attachmentNames[frameIndex]);
/*      */     }
/*      */     
/*      */     private void setAttachment(Skeleton skeleton, Slot slot, String attachmentName) {
/*  968 */       slot.setAttachment((attachmentName == null) ? null : skeleton.getAttachment(this.slotIndex, attachmentName));
/*      */     }
/*      */   }
/*      */   
/*      */   public static class DeformTimeline
/*      */     extends CurveTimeline implements SlotTimeline {
/*      */     int slotIndex;
/*      */     VertexAttachment attachment;
/*      */     private final float[] frames;
/*      */     private final float[][] frameVertices;
/*      */     
/*      */     public DeformTimeline(int frameCount) {
/*  980 */       super(frameCount);
/*  981 */       this.frames = new float[frameCount];
/*  982 */       this.frameVertices = new float[frameCount][];
/*      */     }
/*      */     
/*      */     public int getPropertyId() {
/*  986 */       return (Animation.TimelineType.deform.ordinal() << 27) + this.attachment.getId() + this.slotIndex;
/*      */     }
/*      */     
/*      */     public void setSlotIndex(int index) {
/*  990 */       if (index < 0) throw new IllegalArgumentException("index must be >= 0."); 
/*  991 */       this.slotIndex = index;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getSlotIndex() {
/*  996 */       return this.slotIndex;
/*      */     }
/*      */     
/*      */     public void setAttachment(VertexAttachment attachment) {
/* 1000 */       this.attachment = attachment;
/*      */     }
/*      */ 
/*      */     
/*      */     public VertexAttachment getAttachment() {
/* 1005 */       return this.attachment;
/*      */     }
/*      */ 
/*      */     
/*      */     public float[] getFrames() {
/* 1010 */       return this.frames;
/*      */     }
/*      */ 
/*      */     
/*      */     public float[][] getVertices() {
/* 1015 */       return this.frameVertices;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void setFrame(int frameIndex, float time, float[] vertices) {
/* 1021 */       this.frames[frameIndex] = time;
/* 1022 */       this.frameVertices[frameIndex] = vertices;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> events, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/* 1028 */       Slot slot = (Slot)skeleton.slots.get(this.slotIndex);
/* 1029 */       if (!slot.bone.active)
/* 1030 */         return;  Attachment slotAttachment = slot.attachment;
/* 1031 */       if (!(slotAttachment instanceof VertexAttachment) || ((VertexAttachment)slotAttachment)
/* 1032 */         .getDeformAttachment() != this.attachment)
/*      */         return; 
/* 1034 */       FloatArray deformArray = slot.getDeform();
/* 1035 */       if (deformArray.size == 0) blend = Animation.MixBlend.setup;
/*      */       
/* 1037 */       float[][] frameVertices = this.frameVertices;
/* 1038 */       int vertexCount = (frameVertices[0]).length;
/*      */       
/* 1040 */       float[] frames = this.frames;
/* 1041 */       if (time < frames[0]) {
/* 1042 */         float[] arrayOfFloat; int i; VertexAttachment vertexAttachment = (VertexAttachment)slotAttachment;
/* 1043 */         switch (blend) {
/*      */           case setup:
/* 1045 */             deformArray.clear();
/*      */             return;
/*      */           case first:
/* 1048 */             if (alpha == 1.0F) {
/* 1049 */               deformArray.clear();
/*      */               return;
/*      */             } 
/* 1052 */             arrayOfFloat = deformArray.setSize(vertexCount);
/* 1053 */             if (vertexAttachment.getBones() == null) {
/*      */               
/* 1055 */               float[] setupVertices = vertexAttachment.getVertices();
/* 1056 */               for (int j = 0; j < vertexCount; j++)
/* 1057 */                 arrayOfFloat[j] = arrayOfFloat[j] + (setupVertices[j] - arrayOfFloat[j]) * alpha; 
/*      */               break;
/*      */             } 
/* 1060 */             alpha = 1.0F - alpha;
/* 1061 */             for (i = 0; i < vertexCount; i++) {
/* 1062 */               arrayOfFloat[i] = arrayOfFloat[i] * alpha;
/*      */             }
/*      */             break;
/*      */         } 
/*      */         return;
/*      */       } 
/* 1068 */       float[] deform = deformArray.setSize(vertexCount);
/*      */       
/* 1070 */       if (time >= frames[frames.length - 1]) {
/* 1071 */         float[] lastVertices = frameVertices[frames.length - 1];
/* 1072 */         if (alpha == 1.0F) {
/* 1073 */           if (blend == Animation.MixBlend.add) {
/* 1074 */             VertexAttachment vertexAttachment = (VertexAttachment)slotAttachment;
/* 1075 */             if (vertexAttachment.getBones() == null) {
/*      */               
/* 1077 */               float[] setupVertices = vertexAttachment.getVertices();
/* 1078 */               for (int i = 0; i < vertexCount; i++) {
/* 1079 */                 deform[i] = deform[i] + lastVertices[i] - setupVertices[i];
/*      */               }
/*      */             } else {
/* 1082 */               for (int i = 0; i < vertexCount; i++) {
/* 1083 */                 deform[i] = deform[i] + lastVertices[i];
/*      */               }
/*      */             } 
/*      */           } else {
/* 1087 */             SpineUtils.arraycopy(lastVertices, 0, deform, 0, vertexCount);
/*      */           } 
/*      */         } else {
/* 1090 */           VertexAttachment vertexAttachment1; int i; VertexAttachment vertexAttachment; int j; switch (blend) {
/*      */             case setup:
/* 1092 */               vertexAttachment1 = (VertexAttachment)slotAttachment;
/* 1093 */               if (vertexAttachment1.getBones() == null) {
/*      */                 
/* 1095 */                 float[] setupVertices = vertexAttachment1.getVertices();
/* 1096 */                 for (int k = 0; k < vertexCount; k++) {
/* 1097 */                   float setup = setupVertices[k];
/* 1098 */                   deform[k] = setup + (lastVertices[k] - setup) * alpha;
/*      */                 } 
/*      */                 break;
/*      */               } 
/* 1102 */               for (j = 0; j < vertexCount; j++) {
/* 1103 */                 deform[j] = lastVertices[j] * alpha;
/*      */               }
/*      */               break;
/*      */ 
/*      */             
/*      */             case first:
/*      */             case replace:
/* 1110 */               for (i = 0; i < vertexCount; i++)
/* 1111 */                 deform[i] = deform[i] + (lastVertices[i] - deform[i]) * alpha; 
/*      */               break;
/*      */             case add:
/* 1114 */               vertexAttachment = (VertexAttachment)slotAttachment;
/* 1115 */               if (vertexAttachment.getBones() == null) {
/*      */                 
/* 1117 */                 float[] setupVertices = vertexAttachment.getVertices();
/* 1118 */                 for (int k = 0; k < vertexCount; k++)
/* 1119 */                   deform[k] = deform[k] + (lastVertices[k] - setupVertices[k]) * alpha; 
/*      */                 break;
/*      */               } 
/* 1122 */               for (j = 0; j < vertexCount; j++) {
/* 1123 */                 deform[j] = deform[j] + lastVertices[j] * alpha;
/*      */               }
/*      */               break;
/*      */           } 
/*      */         
/*      */         } 
/*      */         return;
/*      */       } 
/* 1131 */       int frame = Animation.binarySearch(frames, time);
/* 1132 */       float[] prevVertices = frameVertices[frame - 1];
/* 1133 */       float[] nextVertices = frameVertices[frame];
/* 1134 */       float frameTime = frames[frame];
/* 1135 */       float percent = getCurvePercent(frame - 1, 1.0F - (time - frameTime) / (frames[frame - 1] - frameTime));
/*      */       
/* 1137 */       if (alpha == 1.0F) {
/* 1138 */         if (blend == Animation.MixBlend.add) {
/* 1139 */           VertexAttachment vertexAttachment = (VertexAttachment)slotAttachment;
/* 1140 */           if (vertexAttachment.getBones() == null) {
/*      */             
/* 1142 */             float[] setupVertices = vertexAttachment.getVertices();
/* 1143 */             for (int i = 0; i < vertexCount; i++) {
/* 1144 */               float prev = prevVertices[i];
/* 1145 */               deform[i] = deform[i] + prev + (nextVertices[i] - prev) * percent - setupVertices[i];
/*      */             } 
/*      */           } else {
/*      */             
/* 1149 */             for (int i = 0; i < vertexCount; i++) {
/* 1150 */               float prev = prevVertices[i];
/* 1151 */               deform[i] = deform[i] + prev + (nextVertices[i] - prev) * percent;
/*      */             } 
/*      */           } 
/*      */         } else {
/*      */           
/* 1156 */           for (int i = 0; i < vertexCount; i++) {
/* 1157 */             float prev = prevVertices[i];
/* 1158 */             deform[i] = prev + (nextVertices[i] - prev) * percent;
/*      */           } 
/*      */         } 
/*      */       } else {
/* 1162 */         VertexAttachment vertexAttachment1; int i; VertexAttachment vertexAttachment; int j; switch (blend) {
/*      */           case setup:
/* 1164 */             vertexAttachment1 = (VertexAttachment)slotAttachment;
/* 1165 */             if (vertexAttachment1.getBones() == null) {
/*      */               
/* 1167 */               float[] setupVertices = vertexAttachment1.getVertices();
/* 1168 */               for (int k = 0; k < vertexCount; k++) {
/* 1169 */                 float prev = prevVertices[k], setup = setupVertices[k];
/* 1170 */                 deform[k] = setup + (prev + (nextVertices[k] - prev) * percent - setup) * alpha;
/*      */               } 
/*      */               break;
/*      */             } 
/* 1174 */             for (j = 0; j < vertexCount; j++) {
/* 1175 */               float prev = prevVertices[j];
/* 1176 */               deform[j] = (prev + (nextVertices[j] - prev) * percent) * alpha;
/*      */             } 
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           case first:
/*      */           case replace:
/* 1184 */             for (i = 0; i < vertexCount; i++) {
/* 1185 */               float prev = prevVertices[i];
/* 1186 */               deform[i] = deform[i] + (prev + (nextVertices[i] - prev) * percent - deform[i]) * alpha;
/*      */             } 
/*      */             break;
/*      */           case add:
/* 1190 */             vertexAttachment = (VertexAttachment)slotAttachment;
/* 1191 */             if (vertexAttachment.getBones() == null) {
/*      */               
/* 1193 */               float[] setupVertices = vertexAttachment.getVertices();
/* 1194 */               for (int k = 0; k < vertexCount; k++) {
/* 1195 */                 float prev = prevVertices[k];
/* 1196 */                 deform[k] = deform[k] + (prev + (nextVertices[k] - prev) * percent - setupVertices[k]) * alpha;
/*      */               } 
/*      */               break;
/*      */             } 
/* 1200 */             for (j = 0; j < vertexCount; j++) {
/* 1201 */               float prev = prevVertices[j];
/* 1202 */               deform[j] = deform[j] + (prev + (nextVertices[j] - prev) * percent) * alpha;
/*      */             } 
/*      */             break;
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   public static class EventTimeline
/*      */     implements Timeline {
/*      */     private final float[] frames;
/*      */     private final Event[] events;
/*      */     
/*      */     public EventTimeline(int frameCount) {
/* 1216 */       if (frameCount <= 0) throw new IllegalArgumentException("frameCount must be > 0: " + frameCount); 
/* 1217 */       this.frames = new float[frameCount];
/* 1218 */       this.events = new Event[frameCount];
/*      */     }
/*      */     
/*      */     public int getPropertyId() {
/* 1222 */       return Animation.TimelineType.event.ordinal() << 24;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getFrameCount() {
/* 1227 */       return this.frames.length;
/*      */     }
/*      */ 
/*      */     
/*      */     public float[] getFrames() {
/* 1232 */       return this.frames;
/*      */     }
/*      */ 
/*      */     
/*      */     public Event[] getEvents() {
/* 1237 */       return this.events;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setFrame(int frameIndex, Event event) {
/* 1242 */       this.frames[frameIndex] = event.time;
/* 1243 */       this.events[frameIndex] = event;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> firedEvents, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/*      */       int frame;
/* 1250 */       if (firedEvents == null)
/* 1251 */         return;  float[] frames = this.frames;
/* 1252 */       int frameCount = frames.length;
/*      */       
/* 1254 */       if (lastTime > time) {
/* 1255 */         apply(skeleton, lastTime, 2.14748365E9F, firedEvents, alpha, blend, direction);
/* 1256 */         lastTime = -1.0F;
/* 1257 */       } else if (lastTime >= frames[frameCount - 1]) {
/*      */         return;
/* 1259 */       }  if (time < frames[0]) {
/*      */         return;
/*      */       }
/* 1262 */       if (lastTime < frames[0]) {
/* 1263 */         frame = 0;
/*      */       } else {
/* 1265 */         frame = Animation.binarySearch(frames, lastTime);
/* 1266 */         float frameTime = frames[frame];
/* 1267 */         while (frame > 0 && 
/* 1268 */           frames[frame - 1] == frameTime) {
/* 1269 */           frame--;
/*      */         }
/*      */       } 
/* 1272 */       for (; frame < frameCount && time >= frames[frame]; frame++)
/* 1273 */         firedEvents.add(this.events[frame]); 
/*      */     }
/*      */   }
/*      */   
/*      */   public static class DrawOrderTimeline
/*      */     implements Timeline {
/*      */     private final float[] frames;
/*      */     private final int[][] drawOrders;
/*      */     
/*      */     public DrawOrderTimeline(int frameCount) {
/* 1283 */       if (frameCount <= 0) throw new IllegalArgumentException("frameCount must be > 0: " + frameCount); 
/* 1284 */       this.frames = new float[frameCount];
/* 1285 */       this.drawOrders = new int[frameCount][];
/*      */     }
/*      */     
/*      */     public int getPropertyId() {
/* 1289 */       return Animation.TimelineType.drawOrder.ordinal() << 24;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getFrameCount() {
/* 1294 */       return this.frames.length;
/*      */     }
/*      */ 
/*      */     
/*      */     public float[] getFrames() {
/* 1299 */       return this.frames;
/*      */     }
/*      */ 
/*      */     
/*      */     public int[][] getDrawOrders() {
/* 1304 */       return this.drawOrders;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setFrame(int frameIndex, float time, int[] drawOrder) {
/* 1311 */       this.frames[frameIndex] = time;
/* 1312 */       this.drawOrders[frameIndex] = drawOrder;
/*      */     }
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> events, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/*      */       int frame;
/* 1318 */       Array<Slot> drawOrder = skeleton.drawOrder;
/* 1319 */       Array<Slot> slots = skeleton.slots;
/* 1320 */       if (direction == Animation.MixDirection.out) {
/* 1321 */         if (blend == Animation.MixBlend.setup) SpineUtils.arraycopy(slots.items, 0, drawOrder.items, 0, slots.size);
/*      */         
/*      */         return;
/*      */       } 
/* 1325 */       float[] frames = this.frames;
/* 1326 */       if (time < frames[0]) {
/* 1327 */         if (blend == Animation.MixBlend.setup || blend == Animation.MixBlend.first) SpineUtils.arraycopy(slots.items, 0, drawOrder.items, 0, slots.size);
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/* 1332 */       if (time >= frames[frames.length - 1]) {
/* 1333 */         frame = frames.length - 1;
/*      */       } else {
/* 1335 */         frame = Animation.binarySearch(frames, time) - 1;
/*      */       } 
/* 1337 */       int[] drawOrderToSetupIndex = this.drawOrders[frame];
/* 1338 */       if (drawOrderToSetupIndex == null) {
/* 1339 */         SpineUtils.arraycopy(slots.items, 0, drawOrder.items, 0, slots.size);
/*      */       } else {
/* 1341 */         for (int i = 0, n = drawOrderToSetupIndex.length; i < n; i++)
/* 1342 */           drawOrder.set(i, slots.get(drawOrderToSetupIndex[i])); 
/*      */       } 
/*      */     } }
/*      */   public static class IkConstraintTimeline extends CurveTimeline { public static final int ENTRIES = 6; private static final int PREV_TIME = -6; private static final int PREV_MIX = -5;
/*      */     private static final int PREV_SOFTNESS = -4;
/*      */     private static final int PREV_BEND_DIRECTION = -3;
/*      */     private static final int PREV_COMPRESS = -2;
/*      */     private static final int PREV_STRETCH = -1;
/*      */     private static final int MIX = 1;
/*      */     private static final int SOFTNESS = 2;
/*      */     private static final int BEND_DIRECTION = 3;
/*      */     private static final int COMPRESS = 4;
/*      */     private static final int STRETCH = 5;
/*      */     int ikConstraintIndex;
/*      */     private final float[] frames;
/*      */     
/*      */     public IkConstraintTimeline(int frameCount) {
/* 1359 */       super(frameCount);
/* 1360 */       this.frames = new float[frameCount * 6];
/*      */     }
/*      */     
/*      */     public int getPropertyId() {
/* 1364 */       return (Animation.TimelineType.ikConstraint.ordinal() << 24) + this.ikConstraintIndex;
/*      */     }
/*      */     
/*      */     public void setIkConstraintIndex(int index) {
/* 1368 */       if (index < 0) throw new IllegalArgumentException("index must be >= 0."); 
/* 1369 */       this.ikConstraintIndex = index;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getIkConstraintIndex() {
/* 1374 */       return this.ikConstraintIndex;
/*      */     }
/*      */ 
/*      */     
/*      */     public float[] getFrames() {
/* 1379 */       return this.frames;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void setFrame(int frameIndex, float time, float mix, float softness, int bendDirection, boolean compress, boolean stretch) {
/* 1385 */       frameIndex *= 6;
/* 1386 */       this.frames[frameIndex] = time;
/* 1387 */       this.frames[frameIndex + 1] = mix;
/* 1388 */       this.frames[frameIndex + 2] = softness;
/* 1389 */       this.frames[frameIndex + 3] = bendDirection;
/* 1390 */       this.frames[frameIndex + 4] = compress ? 1.0F : 0.0F;
/* 1391 */       this.frames[frameIndex + 5] = stretch ? 1.0F : 0.0F;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> events, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/* 1397 */       IkConstraint constraint = (IkConstraint)skeleton.ikConstraints.get(this.ikConstraintIndex);
/* 1398 */       if (!constraint.active)
/* 1399 */         return;  float[] frames = this.frames;
/* 1400 */       if (time < frames[0]) {
/* 1401 */         switch (blend) {
/*      */           case setup:
/* 1403 */             constraint.mix = constraint.data.mix;
/* 1404 */             constraint.softness = constraint.data.softness;
/* 1405 */             constraint.bendDirection = constraint.data.bendDirection;
/* 1406 */             constraint.compress = constraint.data.compress;
/* 1407 */             constraint.stretch = constraint.data.stretch;
/*      */             return;
/*      */           case first:
/* 1410 */             constraint.mix += (constraint.data.mix - constraint.mix) * alpha;
/* 1411 */             constraint.softness += (constraint.data.softness - constraint.softness) * alpha;
/* 1412 */             constraint.bendDirection = constraint.data.bendDirection;
/* 1413 */             constraint.compress = constraint.data.compress;
/* 1414 */             constraint.stretch = constraint.data.stretch;
/*      */             break;
/*      */         } 
/*      */         return;
/*      */       } 
/* 1419 */       if (time >= frames[frames.length - 6]) {
/* 1420 */         if (blend == Animation.MixBlend.setup) {
/* 1421 */           constraint.mix = constraint.data.mix + (frames[frames.length + -5] - constraint.data.mix) * alpha;
/* 1422 */           constraint.softness = constraint.data.softness + (frames[frames.length + -4] - constraint.data.softness) * alpha;
/*      */           
/* 1424 */           if (direction == Animation.MixDirection.out) {
/* 1425 */             constraint.bendDirection = constraint.data.bendDirection;
/* 1426 */             constraint.compress = constraint.data.compress;
/* 1427 */             constraint.stretch = constraint.data.stretch;
/*      */           } else {
/* 1429 */             constraint.bendDirection = (int)frames[frames.length + -3];
/* 1430 */             constraint.compress = (frames[frames.length + -2] != 0.0F);
/* 1431 */             constraint.stretch = (frames[frames.length + -1] != 0.0F);
/*      */           } 
/*      */         } else {
/* 1434 */           constraint.mix += (frames[frames.length + -5] - constraint.mix) * alpha;
/* 1435 */           constraint.softness += (frames[frames.length + -4] - constraint.softness) * alpha;
/* 1436 */           if (direction == Animation.MixDirection.in) {
/* 1437 */             constraint.bendDirection = (int)frames[frames.length + -3];
/* 1438 */             constraint.compress = (frames[frames.length + -2] != 0.0F);
/* 1439 */             constraint.stretch = (frames[frames.length + -1] != 0.0F);
/*      */           } 
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/* 1446 */       int frame = Animation.binarySearch(frames, time, 6);
/* 1447 */       float mix = frames[frame + -5];
/* 1448 */       float softness = frames[frame + -4];
/* 1449 */       float frameTime = frames[frame];
/* 1450 */       float percent = getCurvePercent(frame / 6 - 1, 1.0F - (time - frameTime) / (frames[frame + -6] - frameTime));
/*      */       
/* 1452 */       if (blend == Animation.MixBlend.setup) {
/* 1453 */         constraint.mix = constraint.data.mix + (mix + (frames[frame + 1] - mix) * percent - constraint.data.mix) * alpha;
/* 1454 */         constraint.softness = constraint.data.softness + (softness + (frames[frame + 2] - softness) * percent - constraint.data.softness) * alpha;
/*      */         
/* 1456 */         if (direction == Animation.MixDirection.out) {
/* 1457 */           constraint.bendDirection = constraint.data.bendDirection;
/* 1458 */           constraint.compress = constraint.data.compress;
/* 1459 */           constraint.stretch = constraint.data.stretch;
/*      */         } else {
/* 1461 */           constraint.bendDirection = (int)frames[frame + -3];
/* 1462 */           constraint.compress = (frames[frame + -2] != 0.0F);
/* 1463 */           constraint.stretch = (frames[frame + -1] != 0.0F);
/*      */         } 
/*      */       } else {
/* 1466 */         constraint.mix += (mix + (frames[frame + 1] - mix) * percent - constraint.mix) * alpha;
/* 1467 */         constraint.softness += (softness + (frames[frame + 2] - softness) * percent - constraint.softness) * alpha;
/* 1468 */         if (direction == Animation.MixDirection.in) {
/* 1469 */           constraint.bendDirection = (int)frames[frame + -3];
/* 1470 */           constraint.compress = (frames[frame + -2] != 0.0F);
/* 1471 */           constraint.stretch = (frames[frame + -1] != 0.0F);
/*      */         } 
/*      */       } 
/*      */     } }
/*      */   public static class TransformConstraintTimeline extends CurveTimeline { public static final int ENTRIES = 5; private static final int PREV_TIME = -5;
/*      */     private static final int PREV_ROTATE = -4;
/*      */     private static final int PREV_TRANSLATE = -3;
/*      */     private static final int PREV_SCALE = -2;
/*      */     private static final int PREV_SHEAR = -1;
/*      */     private static final int ROTATE = 1;
/*      */     private static final int TRANSLATE = 2;
/*      */     private static final int SCALE = 3;
/*      */     private static final int SHEAR = 4;
/*      */     int transformConstraintIndex;
/*      */     private final float[] frames;
/*      */     
/*      */     public TransformConstraintTimeline(int frameCount) {
/* 1488 */       super(frameCount);
/* 1489 */       this.frames = new float[frameCount * 5];
/*      */     }
/*      */     
/*      */     public int getPropertyId() {
/* 1493 */       return (Animation.TimelineType.transformConstraint.ordinal() << 24) + this.transformConstraintIndex;
/*      */     }
/*      */     
/*      */     public void setTransformConstraintIndex(int index) {
/* 1497 */       if (index < 0) throw new IllegalArgumentException("index must be >= 0."); 
/* 1498 */       this.transformConstraintIndex = index;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getTransformConstraintIndex() {
/* 1503 */       return this.transformConstraintIndex;
/*      */     }
/*      */ 
/*      */     
/*      */     public float[] getFrames() {
/* 1508 */       return this.frames;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setFrame(int frameIndex, float time, float rotateMix, float translateMix, float scaleMix, float shearMix) {
/* 1513 */       frameIndex *= 5;
/* 1514 */       this.frames[frameIndex] = time;
/* 1515 */       this.frames[frameIndex + 1] = rotateMix;
/* 1516 */       this.frames[frameIndex + 2] = translateMix;
/* 1517 */       this.frames[frameIndex + 3] = scaleMix;
/* 1518 */       this.frames[frameIndex + 4] = shearMix;
/*      */     }
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> events, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/*      */       float rotate, translate, scale, shear;
/* 1524 */       TransformConstraint constraint = (TransformConstraint)skeleton.transformConstraints.get(this.transformConstraintIndex);
/* 1525 */       if (!constraint.active)
/* 1526 */         return;  float[] frames = this.frames;
/* 1527 */       if (time < frames[0]) {
/* 1528 */         TransformConstraintData data = constraint.data;
/* 1529 */         switch (blend) {
/*      */           case setup:
/* 1531 */             constraint.rotateMix = data.rotateMix;
/* 1532 */             constraint.translateMix = data.translateMix;
/* 1533 */             constraint.scaleMix = data.scaleMix;
/* 1534 */             constraint.shearMix = data.shearMix;
/*      */             return;
/*      */           case first:
/* 1537 */             constraint.rotateMix += (data.rotateMix - constraint.rotateMix) * alpha;
/* 1538 */             constraint.translateMix += (data.translateMix - constraint.translateMix) * alpha;
/* 1539 */             constraint.scaleMix += (data.scaleMix - constraint.scaleMix) * alpha;
/* 1540 */             constraint.shearMix += (data.shearMix - constraint.shearMix) * alpha;
/*      */             break;
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/* 1546 */       if (time >= frames[frames.length - 5]) {
/* 1547 */         int i = frames.length;
/* 1548 */         rotate = frames[i + -4];
/* 1549 */         translate = frames[i + -3];
/* 1550 */         scale = frames[i + -2];
/* 1551 */         shear = frames[i + -1];
/*      */       } else {
/*      */         
/* 1554 */         int frame = Animation.binarySearch(frames, time, 5);
/* 1555 */         rotate = frames[frame + -4];
/* 1556 */         translate = frames[frame + -3];
/* 1557 */         scale = frames[frame + -2];
/* 1558 */         shear = frames[frame + -1];
/* 1559 */         float frameTime = frames[frame];
/* 1560 */         float percent = getCurvePercent(frame / 5 - 1, 1.0F - (time - frameTime) / (frames[frame + -5] - frameTime));
/*      */ 
/*      */         
/* 1563 */         rotate += (frames[frame + 1] - rotate) * percent;
/* 1564 */         translate += (frames[frame + 2] - translate) * percent;
/* 1565 */         scale += (frames[frame + 3] - scale) * percent;
/* 1566 */         shear += (frames[frame + 4] - shear) * percent;
/*      */       } 
/* 1568 */       if (blend == Animation.MixBlend.setup) {
/* 1569 */         TransformConstraintData data = constraint.data;
/* 1570 */         constraint.rotateMix = data.rotateMix + (rotate - data.rotateMix) * alpha;
/* 1571 */         constraint.translateMix = data.translateMix + (translate - data.translateMix) * alpha;
/* 1572 */         constraint.scaleMix = data.scaleMix + (scale - data.scaleMix) * alpha;
/* 1573 */         constraint.shearMix = data.shearMix + (shear - data.shearMix) * alpha;
/*      */       } else {
/* 1575 */         constraint.rotateMix += (rotate - constraint.rotateMix) * alpha;
/* 1576 */         constraint.translateMix += (translate - constraint.translateMix) * alpha;
/* 1577 */         constraint.scaleMix += (scale - constraint.scaleMix) * alpha;
/* 1578 */         constraint.shearMix += (shear - constraint.shearMix) * alpha;
/*      */       } 
/*      */     } }
/*      */ 
/*      */   
/*      */   public static class PathConstraintPositionTimeline
/*      */     extends CurveTimeline
/*      */   {
/*      */     public static final int ENTRIES = 2;
/*      */     static final int PREV_TIME = -2;
/*      */     static final int PREV_VALUE = -1;
/*      */     static final int VALUE = 1;
/*      */     int pathConstraintIndex;
/*      */     final float[] frames;
/*      */     
/*      */     public PathConstraintPositionTimeline(int frameCount) {
/* 1594 */       super(frameCount);
/* 1595 */       this.frames = new float[frameCount * 2];
/*      */     }
/*      */     
/*      */     public int getPropertyId() {
/* 1599 */       return (Animation.TimelineType.pathConstraintPosition.ordinal() << 24) + this.pathConstraintIndex;
/*      */     }
/*      */     
/*      */     public void setPathConstraintIndex(int index) {
/* 1603 */       if (index < 0) throw new IllegalArgumentException("index must be >= 0."); 
/* 1604 */       this.pathConstraintIndex = index;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getPathConstraintIndex() {
/* 1609 */       return this.pathConstraintIndex;
/*      */     }
/*      */ 
/*      */     
/*      */     public float[] getFrames() {
/* 1614 */       return this.frames;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setFrame(int frameIndex, float time, float position) {
/* 1619 */       frameIndex *= 2;
/* 1620 */       this.frames[frameIndex] = time;
/* 1621 */       this.frames[frameIndex + 1] = position;
/*      */     }
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> events, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/*      */       float position;
/* 1627 */       PathConstraint constraint = (PathConstraint)skeleton.pathConstraints.get(this.pathConstraintIndex);
/* 1628 */       if (!constraint.active)
/* 1629 */         return;  float[] frames = this.frames;
/* 1630 */       if (time < frames[0]) {
/* 1631 */         switch (blend) {
/*      */           case setup:
/* 1633 */             constraint.position = constraint.data.position;
/*      */             return;
/*      */           case first:
/* 1636 */             constraint.position += (constraint.data.position - constraint.position) * alpha;
/*      */             break;
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/* 1642 */       if (time >= frames[frames.length - 2]) {
/* 1643 */         position = frames[frames.length + -1];
/*      */       } else {
/*      */         
/* 1646 */         int frame = Animation.binarySearch(frames, time, 2);
/* 1647 */         position = frames[frame + -1];
/* 1648 */         float frameTime = frames[frame];
/* 1649 */         float percent = getCurvePercent(frame / 2 - 1, 1.0F - (time - frameTime) / (frames[frame + -2] - frameTime));
/*      */ 
/*      */         
/* 1652 */         position += (frames[frame + 1] - position) * percent;
/*      */       } 
/* 1654 */       if (blend == Animation.MixBlend.setup) {
/* 1655 */         constraint.position = constraint.data.position + (position - constraint.data.position) * alpha;
/*      */       } else {
/* 1657 */         constraint.position += (position - constraint.position) * alpha;
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   public static class PathConstraintSpacingTimeline extends PathConstraintPositionTimeline {
/*      */     public PathConstraintSpacingTimeline(int frameCount) {
/* 1664 */       super(frameCount);
/*      */     }
/*      */     
/*      */     public int getPropertyId() {
/* 1668 */       return (Animation.TimelineType.pathConstraintSpacing.ordinal() << 24) + this.pathConstraintIndex;
/*      */     }
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> events, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/*      */       float spacing;
/* 1674 */       PathConstraint constraint = (PathConstraint)skeleton.pathConstraints.get(this.pathConstraintIndex);
/* 1675 */       if (!constraint.active)
/* 1676 */         return;  float[] frames = this.frames;
/* 1677 */       if (time < frames[0]) {
/* 1678 */         switch (blend) {
/*      */           case setup:
/* 1680 */             constraint.spacing = constraint.data.spacing;
/*      */             return;
/*      */           case first:
/* 1683 */             constraint.spacing += (constraint.data.spacing - constraint.spacing) * alpha;
/*      */             break;
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/* 1689 */       if (time >= frames[frames.length - 2]) {
/* 1690 */         spacing = frames[frames.length + -1];
/*      */       } else {
/*      */         
/* 1693 */         int frame = Animation.binarySearch(frames, time, 2);
/* 1694 */         spacing = frames[frame + -1];
/* 1695 */         float frameTime = frames[frame];
/* 1696 */         float percent = getCurvePercent(frame / 2 - 1, 1.0F - (time - frameTime) / (frames[frame + -2] - frameTime));
/*      */ 
/*      */         
/* 1699 */         spacing += (frames[frame + 1] - spacing) * percent;
/*      */       } 
/*      */       
/* 1702 */       if (blend == Animation.MixBlend.setup) {
/* 1703 */         constraint.spacing = constraint.data.spacing + (spacing - constraint.data.spacing) * alpha;
/*      */       } else {
/* 1705 */         constraint.spacing += (spacing - constraint.spacing) * alpha;
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   public static class PathConstraintMixTimeline extends CurveTimeline {
/*      */     public static final int ENTRIES = 3;
/*      */     private static final int PREV_TIME = -3;
/*      */     private static final int PREV_ROTATE = -2;
/*      */     private static final int PREV_TRANSLATE = -1;
/*      */     private static final int ROTATE = 1;
/*      */     private static final int TRANSLATE = 2;
/*      */     int pathConstraintIndex;
/*      */     private final float[] frames;
/*      */     
/*      */     public PathConstraintMixTimeline(int frameCount) {
/* 1721 */       super(frameCount);
/* 1722 */       this.frames = new float[frameCount * 3];
/*      */     }
/*      */     
/*      */     public int getPropertyId() {
/* 1726 */       return (Animation.TimelineType.pathConstraintMix.ordinal() << 24) + this.pathConstraintIndex;
/*      */     }
/*      */     
/*      */     public void setPathConstraintIndex(int index) {
/* 1730 */       if (index < 0) throw new IllegalArgumentException("index must be >= 0."); 
/* 1731 */       this.pathConstraintIndex = index;
/*      */     }
/*      */ 
/*      */     
/*      */     public int getPathConstraintIndex() {
/* 1736 */       return this.pathConstraintIndex;
/*      */     }
/*      */ 
/*      */     
/*      */     public float[] getFrames() {
/* 1741 */       return this.frames;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setFrame(int frameIndex, float time, float rotateMix, float translateMix) {
/* 1746 */       frameIndex *= 3;
/* 1747 */       this.frames[frameIndex] = time;
/* 1748 */       this.frames[frameIndex + 1] = rotateMix;
/* 1749 */       this.frames[frameIndex + 2] = translateMix;
/*      */     }
/*      */ 
/*      */     
/*      */     public void apply(Skeleton skeleton, float lastTime, float time, Array<Event> events, float alpha, Animation.MixBlend blend, Animation.MixDirection direction) {
/*      */       float rotate, translate;
/* 1755 */       PathConstraint constraint = (PathConstraint)skeleton.pathConstraints.get(this.pathConstraintIndex);
/* 1756 */       if (!constraint.active)
/* 1757 */         return;  float[] frames = this.frames;
/* 1758 */       if (time < frames[0]) {
/* 1759 */         switch (blend) {
/*      */           case setup:
/* 1761 */             constraint.rotateMix = constraint.data.rotateMix;
/* 1762 */             constraint.translateMix = constraint.data.translateMix;
/*      */             return;
/*      */           case first:
/* 1765 */             constraint.rotateMix += (constraint.data.rotateMix - constraint.rotateMix) * alpha;
/* 1766 */             constraint.translateMix += (constraint.data.translateMix - constraint.translateMix) * alpha;
/*      */             break;
/*      */         } 
/*      */         
/*      */         return;
/*      */       } 
/* 1772 */       if (time >= frames[frames.length - 3]) {
/* 1773 */         rotate = frames[frames.length + -2];
/* 1774 */         translate = frames[frames.length + -1];
/*      */       } else {
/*      */         
/* 1777 */         int frame = Animation.binarySearch(frames, time, 3);
/* 1778 */         rotate = frames[frame + -2];
/* 1779 */         translate = frames[frame + -1];
/* 1780 */         float frameTime = frames[frame];
/* 1781 */         float percent = getCurvePercent(frame / 3 - 1, 1.0F - (time - frameTime) / (frames[frame + -3] - frameTime));
/*      */ 
/*      */         
/* 1784 */         rotate += (frames[frame + 1] - rotate) * percent;
/* 1785 */         translate += (frames[frame + 2] - translate) * percent;
/*      */       } 
/*      */       
/* 1788 */       if (blend == Animation.MixBlend.setup) {
/* 1789 */         constraint.rotateMix = constraint.data.rotateMix + (rotate - constraint.data.rotateMix) * alpha;
/* 1790 */         constraint.translateMix = constraint.data.translateMix + (translate - constraint.data.translateMix) * alpha;
/*      */       } else {
/* 1792 */         constraint.rotateMix += (rotate - constraint.rotateMix) * alpha;
/* 1793 */         constraint.translateMix += (translate - constraint.translateMix) * alpha;
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   public static interface SlotTimeline extends Timeline {
/*      */     void setSlotIndex(int param1Int);
/*      */     
/*      */     int getSlotIndex();
/*      */   }
/*      */   
/*      */   public static interface BoneTimeline extends Timeline {
/*      */     void setBoneIndex(int param1Int);
/*      */     
/*      */     int getBoneIndex();
/*      */   }
/*      */   
/*      */   public static interface Timeline {
/*      */     void apply(Skeleton param1Skeleton, float param1Float1, float param1Float2, Array<Event> param1Array, float param1Float3, Animation.MixBlend param1MixBlend, Animation.MixDirection param1MixDirection);
/*      */     
/*      */     int getPropertyId();
/*      */   }
/*      */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\Animation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */