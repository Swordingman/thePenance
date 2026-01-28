/*      */ package com.esotericsoftware.spine38;
/*      */ 
/*      */ import com.badlogic.gdx.utils.Array;
/*      */ import com.badlogic.gdx.utils.FloatArray;
/*      */ import com.badlogic.gdx.utils.IntArray;
/*      */ import com.badlogic.gdx.utils.IntSet;
/*      */ import com.badlogic.gdx.utils.Pool;

import static com.esotericsoftware.spine38.AnimationState.EventType.interrupt;
import static com.esotericsoftware.spine38.AnimationState.EventType.start;

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
/*      */ public class AnimationState
/*      */ {
/*   54 */   private static final Animation emptyAnimation = new Animation("<empty>", new Array(0), 0.0F);
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int SUBSEQUENT = 0;
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FIRST = 1;
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int HOLD = 2;
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int HOLD_MIX = 3;
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int SETUP = 1;
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int CURRENT = 2;
/*      */ 
/*      */   
/*      */   private AnimationStateData data;
/*      */ 
/*      */   
/*   84 */   final Array<TrackEntry> tracks = new Array();
/*   85 */   private final Array<Event> events = new Array();
/*   86 */   final Array<AnimationStateListener> listeners = new Array();
/*   87 */   private final EventQueue queue = new EventQueue();
/*   88 */   private final IntSet propertyIDs = new IntSet();
/*      */   boolean animationsChanged;
/*   90 */   private float timeScale = 1.0F;
/*      */   private int unkeyedState;
/*      */   
/*   93 */   final Pool<TrackEntry> trackEntryPool = new Pool() {
/*      */       protected Object newObject() {
/*   95 */         return new AnimationState.TrackEntry();
/*      */       }
/*      */     };
/*      */ 
/*      */   
/*      */   public AnimationState() {}
/*      */ 
/*      */   
/*      */   public AnimationState(AnimationStateData data) {
/*  104 */     if (data == null) throw new IllegalArgumentException("data cannot be null."); 
/*  105 */     this.data = data;
/*      */   }
/*      */ 
/*      */   
/*      */   public void update(float delta) {
/*  110 */     delta *= this.timeScale;
/*  111 */     for (int i = 0, n = this.tracks.size; i < n; i++) {
/*  112 */       TrackEntry current = (TrackEntry)this.tracks.get(i);
/*  113 */       if (current == null)
/*      */         continue; 
/*  115 */       current.animationLast = current.nextAnimationLast;
/*  116 */       current.trackLast = current.nextTrackLast;
/*      */       
/*  118 */       float currentDelta = delta * current.timeScale;
/*      */       
/*  120 */       if (current.delay > 0.0F) {
/*  121 */         current.delay -= currentDelta;
/*  122 */         if (current.delay > 0.0F)
/*  123 */           continue;  currentDelta = -current.delay;
/*  124 */         current.delay = 0.0F;
/*      */       } 
/*      */       
/*  127 */       TrackEntry next = current.next;
/*  128 */       if (next != null) {
/*      */         
/*  130 */         float nextTime = current.trackLast - next.delay;
/*  131 */         if (nextTime >= 0.0F) {
/*  132 */           next.delay = 0.0F;
/*  133 */           next.trackTime += (current.timeScale == 0.0F) ? 0.0F : ((nextTime / current.timeScale + delta) * next.timeScale);
/*  134 */           current.trackTime += currentDelta;
/*  135 */           setCurrent(i, next, true);
/*  136 */           while (next.mixingFrom != null) {
/*  137 */             next.mixTime += delta;
/*  138 */             next = next.mixingFrom;
/*      */           } 
/*      */           continue;
/*      */         } 
/*  142 */       } else if (current.trackLast >= current.trackEnd && current.mixingFrom == null) {
/*      */         
/*  144 */         this.tracks.set(i, null);
/*  145 */         this.queue.end(current);
/*  146 */         disposeNext(current);
/*      */         continue;
/*      */       } 
/*  149 */       if (current.mixingFrom != null && updateMixingFrom(current, delta)) {
/*      */         
/*  151 */         TrackEntry from = current.mixingFrom;
/*  152 */         current.mixingFrom = null;
/*  153 */         if (from != null) from.mixingTo = null; 
/*  154 */         while (from != null) {
/*  155 */           this.queue.end(from);
/*  156 */           from = from.mixingFrom;
/*      */         } 
/*      */       } 
/*      */       
/*  160 */       current.trackTime += currentDelta;
/*      */       continue;
/*      */     } 
/*  163 */     this.queue.drain();
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean updateMixingFrom(TrackEntry to, float delta) {
/*  168 */     TrackEntry from = to.mixingFrom;
/*  169 */     if (from == null) return true;
/*      */     
/*  171 */     boolean finished = updateMixingFrom(from, delta);
/*      */     
/*  173 */     from.animationLast = from.nextAnimationLast;
/*  174 */     from.trackLast = from.nextTrackLast;
/*      */ 
/*      */     
/*  177 */     if (to.mixTime > 0.0F && to.mixTime >= to.mixDuration) {
/*      */       
/*  179 */       if (from.totalAlpha == 0.0F || to.mixDuration == 0.0F) {
/*  180 */         to.mixingFrom = from.mixingFrom;
/*  181 */         if (from.mixingFrom != null) from.mixingFrom.mixingTo = to; 
/*  182 */         to.interruptAlpha = from.interruptAlpha;
/*  183 */         this.queue.end(from);
/*      */       } 
/*  185 */       return finished;
/*      */     } 
/*      */     
/*  188 */     from.trackTime += delta * from.timeScale;
/*  189 */     to.mixTime += delta;
/*  190 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean apply(Skeleton skeleton) {
/*  197 */     if (skeleton == null) throw new IllegalArgumentException("skeleton cannot be null."); 
/*  198 */     if (this.animationsChanged) animationsChanged();
/*      */     
/*  200 */     Array<Event> events = this.events;
/*  201 */     boolean applied = false;
/*  202 */     for (int i = 0, n = this.tracks.size; i < n; i++) {
/*  203 */       TrackEntry current = (TrackEntry)this.tracks.get(i);
/*  204 */       if (current != null && current.delay <= 0.0F) {
/*  205 */         applied = true;
/*      */ 
/*      */         
/*  208 */         Animation.MixBlend blend = (i == 0) ? Animation.MixBlend.first : current.mixBlend;
/*      */ 
/*      */         
/*  211 */         float mix = current.alpha;
/*  212 */         if (current.mixingFrom != null) {
/*  213 */           mix *= applyMixingFrom(current, skeleton, blend);
/*  214 */         } else if (current.trackTime >= current.trackEnd && current.next == null) {
/*  215 */           mix = 0.0F;
/*      */         } 
/*      */         
/*  218 */         float animationLast = current.animationLast, animationTime = current.getAnimationTime();
/*  219 */         int timelineCount = current.animation.timelines.size;
/*  220 */         Object[] timelines = current.animation.timelines.items;
/*  221 */         if ((i == 0 && mix == 1.0F) || blend == Animation.MixBlend.add)
/*  222 */         { for (int ii = 0; ii < timelineCount; ii++) {
/*  223 */             Object timeline = timelines[ii];
/*  224 */             if (timeline instanceof Animation.AttachmentTimeline) {
/*  225 */               applyAttachmentTimeline((Animation.AttachmentTimeline)timeline, skeleton, animationTime, blend, true);
/*      */             } else {
/*  227 */               ((Animation.Timeline)timeline).apply(skeleton, animationLast, animationTime, events, mix, blend, Animation.MixDirection.in);
/*      */             } 
/*      */           }  }
/*  230 */         else { int[] timelineMode = current.timelineMode.items;
/*      */           
/*  232 */           boolean firstFrame = (current.timelinesRotation.size != timelineCount << 1);
/*  233 */           if (firstFrame) current.timelinesRotation.setSize(timelineCount << 1); 
/*  234 */           float[] timelinesRotation = current.timelinesRotation.items;
/*      */           
/*  236 */           for (int ii = 0; ii < timelineCount; ii++) {
/*  237 */             Animation.Timeline timeline = (Animation.Timeline)timelines[ii];
/*  238 */             Animation.MixBlend timelineBlend = (timelineMode[ii] == 0) ? blend : Animation.MixBlend.setup;
/*  239 */             if (timeline instanceof Animation.RotateTimeline) {
/*  240 */               applyRotateTimeline((Animation.RotateTimeline)timeline, skeleton, animationTime, mix, timelineBlend, timelinesRotation, ii << 1, firstFrame);
/*      */             }
/*  242 */             else if (timeline instanceof Animation.AttachmentTimeline) {
/*  243 */               applyAttachmentTimeline((Animation.AttachmentTimeline)timeline, skeleton, animationTime, blend, true);
/*      */             } else {
/*  245 */               timeline.apply(skeleton, animationLast, animationTime, events, mix, timelineBlend, Animation.MixDirection.in);
/*      */             } 
/*      */           }  }
/*  248 */          queueEvents(current, animationTime);
/*  249 */         events.clear();
/*  250 */         current.nextAnimationLast = animationTime;
/*  251 */         current.nextTrackLast = current.trackTime;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  257 */     int setupState = this.unkeyedState + 1;
/*  258 */     Object[] slots = skeleton.slots.items;
/*  259 */     for (int j = 0, k = skeleton.slots.size; j < k; j++) {
/*  260 */       Slot slot = (Slot)slots[j];
/*  261 */       if (slot.attachmentState == setupState) {
/*  262 */         String attachmentName = slot.data.attachmentName;
/*  263 */         slot.setAttachment((attachmentName == null) ? null : skeleton.getAttachment(slot.data.index, attachmentName));
/*      */       } 
/*      */     } 
/*  266 */     this.unkeyedState += 2;
/*      */     
/*  268 */     this.queue.drain();
/*  269 */     return applied;
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
/* */   private float applyMixingFrom(TrackEntry to, Skeleton skeleton, Animation.MixBlend blend) {
    /* 273 */     TrackEntry from = to.mixingFrom;
    /* 274 */     if (from.mixingFrom != null) applyMixingFrom(from, skeleton, blend);
    /* */
    /* 276 */     float mix;
    /* 277 */     if (to.mixDuration == 0) {
        /* 278 */       mix = 1.0F;
        /* 279 */       if (blend == Animation.MixBlend.first) blend = Animation.MixBlend.setup;
        /* */     } else {
        /* 281 */       mix = to.mixTime / to.mixDuration;
        /* 282 */       if (mix > 1.0F) mix = 1.0F;
        /* 283 */       if (blend != Animation.MixBlend.first) blend = from.mixBlend;
        /* */     }
    /* */
    /* 286 */     Array<Event> events = (mix < from.eventThreshold) ? this.events : null;
    /* 287 */     boolean attachments = (mix < from.attachmentThreshold);
    /* 288 */     boolean drawOrder = (mix < from.drawOrderThreshold);
    /* 289 */     float animationLast = from.animationLast;
    /* 290 */     float animationTime = from.getAnimationTime();
    /* 291 */     int timelineCount = from.animation.timelines.size;
    /* 292 */     Object[] timelines = from.animation.timelines.items;
    /* 293 */     float alphaHold = from.alpha * to.interruptAlpha;
    /* 294 */     float alphaMix = alphaHold * (1.0F - mix);
    /* */
    /* 296 */     if (blend == Animation.MixBlend.add) {
        /* 297 */       for (int i = 0; i < timelineCount; i++)
            /* 298 */         ((Animation.Timeline)timelines[i]).apply(skeleton, animationLast, animationTime, events, alphaMix, blend, Animation.MixDirection.out);
        /* */     } else {
        /* 300 */       int[] timelineMode = from.timelineMode.items;
        /* 301 */       Object[] timelineHoldMix = from.timelineHoldMix.items;
        /* */
        /* 303 */       boolean firstFrame = false;
        /* 304 */       if (from.timelinesRotation.size != timelineCount << 1) {
            /* 305 */         from.timelinesRotation.setSize(timelineCount << 1);
            /* 306 */         firstFrame = true;
            /* */       }
        /* 308 */       float[] timelinesRotation = from.timelinesRotation.items;
        /* */
        /* 310 */       from.totalAlpha = 0.0F;
        /* 311 */       for (int i = 0; i < timelineCount; i++) {
            /* 312 */         Animation.Timeline timeline = (Animation.Timeline)timelines[i];
            /* 313 */         Animation.MixDirection direction = Animation.MixDirection.out;
            /* */         Animation.MixBlend timelineBlend;
            /* */         float alpha;
            /* */
            /* 317 */         switch (timelineMode[i]) {
                /* */           case 0:
                    /* 319 */             if (!drawOrder && timeline instanceof Animation.DrawOrderTimeline) continue; // break loop iteration
                    /* 320 */             timelineBlend = blend;
                    /* 321 */             alpha = alphaMix;
                    /* */             break;
                /* */           case 1:
                    /* 324 */             timelineBlend = Animation.MixBlend.setup;
                    /* 325 */             alpha = alphaMix;
                    /* */             break;
                /* */           case 2:
                    /* 328 */             timelineBlend = Animation.MixBlend.setup;
                    /* 329 */             alpha = alphaHold;
                    /* */             break;
                /* */           default:
                    /* 332 */             timelineBlend = Animation.MixBlend.setup;
                    /* 333 */             TrackEntry holdMix = (TrackEntry)timelineHoldMix[i];
                    /* 334 */             alpha = alphaHold * Math.max(0.0F, 1.0F - holdMix.mixTime / holdMix.mixDuration);
                    /* */             break;
                /* */         }
            /* */
            /* 338 */         from.totalAlpha += alpha;
            /* 339 */         if (timeline instanceof Animation.RotateTimeline) {
                /* 340 */           applyRotateTimeline((Animation.RotateTimeline)timeline, skeleton, animationTime, alpha, timelineBlend, timelinesRotation, i << 1, firstFrame);
                /* 341 */         } else if (timeline instanceof Animation.AttachmentTimeline) {
                /* 342 */           applyAttachmentTimeline((Animation.AttachmentTimeline)timeline, skeleton, animationTime, timelineBlend, attachments);
                /* */         } else {
                /* 344 */           if (drawOrder && timeline instanceof Animation.DrawOrderTimeline && timelineBlend == Animation.MixBlend.setup)
                    /* 345 */             direction = Animation.MixDirection.in;
                /* 346 */           timeline.apply(skeleton, animationLast, animationTime, events, alpha, timelineBlend, direction);
                /* */         }
            /* */       }
        /* */     }
    /* */
    /* 351 */     if (to.mixDuration > 0.0F) queueEvents(from, animationTime);
    /* 352 */     this.events.clear();
    /* 353 */     from.nextAnimationLast = animationTime;
    /* 354 */     from.nextTrackLast = from.trackTime;
    /* */
    /* 356 */     return mix;
    /* */   }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void applyAttachmentTimeline(Animation.AttachmentTimeline timeline, Skeleton skeleton, float time, Animation.MixBlend blend, boolean attachments) {
/*  359 */     Slot slot = (Slot)skeleton.slots.get(timeline.slotIndex);
/*  360 */     if (!slot.bone.active)
/*      */       return; 
/*  362 */     float[] frames = timeline.frames;
/*  363 */     if (time < frames[0]) {
/*  364 */       if (blend == Animation.MixBlend.setup || blend == Animation.MixBlend.first)
/*  365 */         setAttachment(skeleton, slot, slot.data.attachmentName, attachments); 
/*      */     } else {
/*      */       int frameIndex;
/*  368 */       if (time >= frames[frames.length - 1]) {
/*  369 */         frameIndex = frames.length - 1;
/*      */       } else {
/*  371 */         frameIndex = Animation.binarySearch(frames, time) - 1;
/*  372 */       }  setAttachment(skeleton, slot, timeline.attachmentNames[frameIndex], attachments);
/*      */     } 
/*      */ 
/*      */     
/*  376 */     if (slot.attachmentState <= this.unkeyedState) slot.attachmentState = this.unkeyedState + 1; 
/*      */   }
/*      */   
/*      */   private void setAttachment(Skeleton skeleton, Slot slot, String attachmentName, boolean attachments) {
/*  380 */     slot.setAttachment((attachmentName == null) ? null : skeleton.getAttachment(slot.data.index, attachmentName));
/*  381 */     if (attachments) slot.attachmentState = this.unkeyedState + 2;
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */
/* */   private void applyRotateTimeline(Animation.RotateTimeline timeline, Skeleton skeleton, float time, float alpha, Animation.MixBlend blend, float[] timelinesRotation, int i, boolean firstFrame) {
    /* 389 */     if (firstFrame) timelinesRotation[i] = 0.0F;
    /* */
    /* 391 */     if (alpha == 1.0F) {
        /* 392 */       timeline.apply(skeleton, 0.0F, time, null, 1.0F, blend, Animation.MixDirection.in);
        /* 393 */       return;
        /* */     }
    /* */
    /* 396 */     Bone bone = (Bone)skeleton.bones.get(timeline.boneIndex);
    /* 397 */     if (!bone.active) return;
    /* */
    /* 398 */     float[] frames = timeline.frames;
    /* 399 */     float r1, r2;
    /* */
    /* 400 */     if (time < frames[0]) {
        // --- 修正点：枚举名称修正 (start -> setup, interrupt -> first) ---
        /* 401 */       switch (blend) {
            /* */         case setup:
                /* 403 */           bone.rotation = bone.data.rotation;
                /* 404 */           return;
            /* */         case first:
                /* 405 */           r1 = bone.rotation;
                /* 406 */           r2 = bone.data.rotation;
                /* 407 */           break;
            /* */         default:
                /* 408 */           return;
            /* */       }
        /* */     } else {
        /* 412 */       r1 = (blend == Animation.MixBlend.setup) ? bone.data.rotation : bone.rotation;
        /* 413 */       if (time >= frames[frames.length - 2]) {
            /* 414 */         r2 = bone.data.rotation + frames[frames.length - 1];
            /* */       } else {
            /* 417 */         int frame = Animation.binarySearch(frames, time, 2);
            /* 418 */         float prevRotation = frames[frame - 1];
            /* 419 */         float frameTime = frames[frame];
            /* 420 */         float percent = timeline.getCurvePercent((frame >> 1) - 1, 1.0F - (time - frameTime) / (frames[frame - 2] - frameTime));
            /* */
            /* 423 */         r2 = frames[frame + 1] - prevRotation;
            /* 424 */         r2 -= (16384 - (int)(16384.499999999996D - r2 / 360.0F)) * 360; // 角度归一化
            /* 425 */         r2 = prevRotation + r2 * percent + bone.data.rotation;
            /* 426 */         r2 -= (16384 - (int)(16384.499999999996D - r2 / 360.0F)) * 360;
            /* */       }
        /* */     }
    /* */
    /* 431 */     float diff = r2 - r1;
    /* 432 */     diff -= (16384 - (int)(16384.499999999996D - diff / 360.0F)) * 360;
    /* */
    /* 434 */     float total;
    /* 435 */     if (diff == 0.0F) {
        /* 436 */       total = timelinesRotation[i];
        /* */     } else {
        /* 438 */       float lastTotal, lastDiff;
        /* 439 */       if (firstFrame) {
            /* 440 */         lastTotal = 0.0F;
            /* 441 */         lastDiff = diff;
            /* */       } else {
            /* 443 */         lastTotal = timelinesRotation[i];
            /* 444 */         lastDiff = timelinesRotation[i + 1];
            /* */       }
        /* 446 */       boolean current = (diff > 0.0F);
        /* 447 */       boolean dir = (lastTotal >= 0.0F);
        /* */
        /* 449 */       if (Math.signum(lastDiff) != Math.signum(diff) && Math.abs(lastDiff) <= 90.0F) {
            /* 450 */         if (Math.abs(lastTotal) > 180.0F) lastTotal += 360.0F * Math.signum(lastTotal);
            /* 451 */         dir = current;
            /* */       }
        /* */
        /* 454 */       total = diff + lastTotal - lastTotal % 360.0F;
        /* 455 */       if (dir != current) total += 360.0F * Math.signum(lastTotal);
        /* 456 */       timelinesRotation[i] = total;
        /* */     }
    /* */
    /* 459 */     timelinesRotation[i + 1] = diff;
    /* 460 */     r1 += total * alpha;
    /* 461 */     bone.rotation = r1 - (16384 - (int)(16384.499999999996D - r1 / 360.0F)) * 360;
    /* */   }
/*      */   private void queueEvents(TrackEntry entry, float animationTime) {
/*      */     boolean complete;
/*  461 */     float animationStart = entry.animationStart, animationEnd = entry.animationEnd;
/*  462 */     float duration = animationEnd - animationStart;
/*  463 */     float trackLastWrapped = entry.trackLast % duration;
/*      */ 
/*      */     
/*  466 */     Array<Event> events = this.events;
/*  467 */     int i = 0, n = events.size;
/*  468 */     for (; i < n; i++) {
/*  469 */       Event event = (Event)events.get(i);
/*  470 */       if (event.time < trackLastWrapped)
/*  471 */         break;  if (event.time <= animationEnd) {
/*  472 */         this.queue.event(entry, event);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  477 */     if (entry.loop) {
/*  478 */       complete = (duration == 0.0F || trackLastWrapped > entry.trackTime % duration);
/*      */     } else {
/*  480 */       complete = (animationTime >= animationEnd && entry.animationLast < animationEnd);
/*  481 */     }  if (complete) this.queue.complete(entry);
/*      */ 
/*      */     
/*  484 */     for (; i < n; i++) {
/*  485 */       Event event = (Event)events.get(i);
/*  486 */       if (event.time >= animationStart) {
/*  487 */         this.queue.event(entry, (Event)events.get(i));
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearTracks() {
/*  496 */     boolean oldDrainDisabled = this.queue.drainDisabled;
/*  497 */     this.queue.drainDisabled = true;
/*  498 */     for (int i = 0, n = this.tracks.size; i < n; i++)
/*  499 */       clearTrack(i); 
/*  500 */     this.tracks.clear();
/*  501 */     this.queue.drainDisabled = oldDrainDisabled;
/*  502 */     this.queue.drain();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearTrack(int trackIndex) {
/*  510 */     if (trackIndex < 0) throw new IllegalArgumentException("trackIndex must be >= 0."); 
/*  511 */     if (trackIndex >= this.tracks.size)
/*  512 */       return;  TrackEntry current = (TrackEntry)this.tracks.get(trackIndex);
/*  513 */     if (current == null)
/*      */       return; 
/*  515 */     this.queue.end(current);
/*      */     
/*  517 */     disposeNext(current);
/*      */     
/*  519 */     TrackEntry entry = current;
/*      */     while (true) {
/*  521 */       TrackEntry from = entry.mixingFrom;
/*  522 */       if (from == null)
/*  523 */         break;  this.queue.end(from);
/*  524 */       entry.mixingFrom = null;
/*  525 */       entry.mixingTo = null;
/*  526 */       entry = from;
/*      */     } 
/*      */     
/*  529 */     this.tracks.set(current.trackIndex, null);
/*      */     
/*  531 */     this.queue.drain();
/*      */   }
/*      */   
/*      */   private void setCurrent(int index, TrackEntry current, boolean interrupt) {
/*  535 */     TrackEntry from = expandToIndex(index);
/*  536 */     this.tracks.set(index, current);
/*      */     
/*  538 */     if (from != null) {
/*  539 */       if (interrupt) this.queue.interrupt(from); 
/*  540 */       current.mixingFrom = from;
/*  541 */       from.mixingTo = current;
/*  542 */       current.mixTime = 0.0F;
/*      */ 
/*      */       
/*  545 */       if (from.mixingFrom != null && from.mixDuration > 0.0F) {
/*  546 */         current.interruptAlpha *= Math.min(1.0F, from.mixTime / from.mixDuration);
/*      */       }
/*  548 */       from.timelinesRotation.clear();
/*      */     } 
/*      */     
/*  551 */     this.queue.start(current);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TrackEntry setAnimation(int trackIndex, String animationName, boolean loop) {
/*  558 */     Animation animation = this.data.skeletonData.findAnimation(animationName);
/*  559 */     if (animation == null) throw new IllegalArgumentException("Animation not found: " + animationName); 
/*  560 */     return setAnimation(trackIndex, animation, loop);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TrackEntry setAnimation(int trackIndex, Animation animation, boolean loop) {
/*  570 */     if (trackIndex < 0) throw new IllegalArgumentException("trackIndex must be >= 0."); 
/*  571 */     if (animation == null) throw new IllegalArgumentException("animation cannot be null."); 
/*  572 */     boolean interrupt = true;
/*  573 */     TrackEntry current = expandToIndex(trackIndex);
/*  574 */     if (current != null)
/*  575 */       if (current.nextTrackLast == -1.0F) {
/*      */         
/*  577 */         this.tracks.set(trackIndex, current.mixingFrom);
/*  578 */         this.queue.interrupt(current);
/*  579 */         this.queue.end(current);
/*  580 */         disposeNext(current);
/*  581 */         current = current.mixingFrom;
/*  582 */         interrupt = false;
/*      */       } else {
/*  584 */         disposeNext(current);
/*      */       }  
/*  586 */     TrackEntry entry = trackEntry(trackIndex, animation, loop, current);
/*  587 */     setCurrent(trackIndex, entry, interrupt);
/*  588 */     this.queue.drain();
/*  589 */     return entry;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TrackEntry addAnimation(int trackIndex, String animationName, boolean loop, float delay) {
/*  596 */     Animation animation = this.data.skeletonData.findAnimation(animationName);
/*  597 */     if (animation == null) throw new IllegalArgumentException("Animation not found: " + animationName); 
/*  598 */     return addAnimation(trackIndex, animation, loop, delay);
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
/*      */   public TrackEntry addAnimation(int trackIndex, Animation animation, boolean loop, float delay) {
/*  610 */     if (trackIndex < 0) throw new IllegalArgumentException("trackIndex must be >= 0."); 
/*  611 */     if (animation == null) throw new IllegalArgumentException("animation cannot be null.");
/*      */     
/*  613 */     TrackEntry last = expandToIndex(trackIndex);
/*  614 */     if (last != null) {
/*  615 */       while (last.next != null) {
/*  616 */         last = last.next;
/*      */       }
/*      */     }
/*  619 */     TrackEntry entry = trackEntry(trackIndex, animation, loop, last);
/*      */     
/*  621 */     if (last == null) {
/*  622 */       setCurrent(trackIndex, entry, true);
/*  623 */       this.queue.drain();
/*      */     } else {
/*  625 */       last.next = entry;
/*  626 */       if (delay <= 0.0F) {
/*  627 */         float duration = last.animationEnd - last.animationStart;
/*  628 */         if (duration != 0.0F) {
/*  629 */           if (last.loop) {
/*  630 */             delay += duration * (1 + (int)(last.trackTime / duration));
/*      */           } else {
/*  632 */             delay += Math.max(duration, last.trackTime);
/*  633 */           }  delay -= this.data.getMix(last.animation, animation);
/*      */         } else {
/*  635 */           delay = last.trackTime;
/*      */         } 
/*      */       } 
/*      */     } 
/*  639 */     entry.delay = delay;
/*  640 */     return entry;
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
/*      */   public TrackEntry setEmptyAnimation(int trackIndex, float mixDuration) {
/*  658 */     TrackEntry entry = setAnimation(trackIndex, emptyAnimation, false);
/*  659 */     entry.mixDuration = mixDuration;
/*  660 */     entry.trackEnd = mixDuration;
/*  661 */     return entry;
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
/*      */   public TrackEntry addEmptyAnimation(int trackIndex, float mixDuration, float delay) {
/*  676 */     if (delay <= 0.0F) delay -= mixDuration; 
/*  677 */     TrackEntry entry = addAnimation(trackIndex, emptyAnimation, false, delay);
/*  678 */     entry.mixDuration = mixDuration;
/*  679 */     entry.trackEnd = mixDuration;
/*  680 */     return entry;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setEmptyAnimations(float mixDuration) {
/*  686 */     boolean oldDrainDisabled = this.queue.drainDisabled;
/*  687 */     this.queue.drainDisabled = true;
/*  688 */     for (int i = 0, n = this.tracks.size; i < n; i++) {
/*  689 */       TrackEntry current = (TrackEntry)this.tracks.get(i);
/*  690 */       if (current != null) setEmptyAnimation(current.trackIndex, mixDuration); 
/*      */     } 
/*  692 */     this.queue.drainDisabled = oldDrainDisabled;
/*  693 */     this.queue.drain();
/*      */   }
/*      */   
/*      */   private TrackEntry expandToIndex(int index) {
/*  697 */     if (index < this.tracks.size) return (TrackEntry)this.tracks.get(index); 
/*  698 */     this.tracks.ensureCapacity(index - this.tracks.size + 1);
/*  699 */     this.tracks.size = index + 1;
/*  700 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private TrackEntry trackEntry(int trackIndex, Animation animation, boolean loop, TrackEntry last) {
/*  705 */     TrackEntry entry = (TrackEntry)this.trackEntryPool.obtain();
/*  706 */     entry.trackIndex = trackIndex;
/*  707 */     entry.animation = animation;
/*  708 */     entry.loop = loop;
/*  709 */     entry.holdPrevious = false;
/*      */     
/*  711 */     entry.eventThreshold = 0.0F;
/*  712 */     entry.attachmentThreshold = 0.0F;
/*  713 */     entry.drawOrderThreshold = 0.0F;
/*      */     
/*  715 */     entry.animationStart = 0.0F;
/*  716 */     entry.animationEnd = animation.getDuration();
/*  717 */     entry.animationLast = -1.0F;
/*  718 */     entry.nextAnimationLast = -1.0F;
/*      */     
/*  720 */     entry.delay = 0.0F;
/*  721 */     entry.trackTime = 0.0F;
/*  722 */     entry.trackLast = -1.0F;
/*  723 */     entry.nextTrackLast = -1.0F;
/*  724 */     entry.trackEnd = Float.MAX_VALUE;
/*  725 */     entry.timeScale = 1.0F;
/*      */     
/*  727 */     entry.alpha = 1.0F;
/*  728 */     entry.interruptAlpha = 1.0F;
/*  729 */     entry.mixTime = 0.0F;
/*  730 */     entry.mixDuration = (last == null) ? 0.0F : this.data.getMix(last.animation, animation);
/*  731 */     return entry;
/*      */   }
/*      */   
/*      */   private void disposeNext(TrackEntry entry) {
/*  735 */     TrackEntry next = entry.next;
/*  736 */     while (next != null) {
/*  737 */       this.queue.dispose(next);
/*  738 */       next = next.next;
/*      */     } 
/*  740 */     entry.next = null;
/*      */   }
/*      */   
/*      */   void animationsChanged() {
/*  744 */     this.animationsChanged = false;
/*      */ 
/*      */     
/*  747 */     this.propertyIDs.clear(2048);
/*  748 */     for (int i = 0, n = this.tracks.size; i < n; i++) {
/*  749 */       TrackEntry entry = (TrackEntry)this.tracks.get(i);
/*  750 */       if (entry != null) {
/*  751 */         while (entry.mixingFrom != null)
/*  752 */           entry = entry.mixingFrom; 
/*      */         do {
/*  754 */           if (entry.mixingTo == null || entry.mixBlend != Animation.MixBlend.add) computeHold(entry); 
/*  755 */           entry = entry.mixingTo;
/*  756 */         } while (entry != null);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   private void computeHold(TrackEntry entry) {
/*  761 */     TrackEntry to = entry.mixingTo;
/*  762 */     Object[] timelines = entry.animation.timelines.items;
/*  763 */     int timelinesCount = entry.animation.timelines.size;
/*  764 */     int[] timelineMode = entry.timelineMode.setSize(timelinesCount);
/*  765 */     entry.timelineHoldMix.clear();
/*  766 */     Object[] timelineHoldMix = entry.timelineHoldMix.setSize(timelinesCount);
/*  767 */     IntSet propertyIDs = this.propertyIDs;
/*      */     
/*  769 */     if (to != null && to.holdPrevious) {
/*  770 */       for (int j = 0; j < timelinesCount; j++) {
/*  771 */         propertyIDs.add(((Animation.Timeline)timelines[j]).getPropertyId());
/*  772 */         timelineMode[j] = 2;
/*      */       } 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  778 */     for (int i = 0; i < timelinesCount; i++) {
/*  779 */       Animation.Timeline timeline = (Animation.Timeline)timelines[i];
/*  780 */       int id = timeline.getPropertyId();
/*  781 */       if (!propertyIDs.add(id)) {
/*  782 */         timelineMode[i] = 0;
/*  783 */       } else if (to == null || timeline instanceof Animation.AttachmentTimeline || timeline instanceof Animation.DrawOrderTimeline || timeline instanceof Animation.EventTimeline || 
/*  784 */         !to.animation.hasTimeline(id)) {
/*  785 */         timelineMode[i] = 1;
/*      */       } else {
/*  787 */         TrackEntry next = to.mixingTo; while (true) { if (next != null) {
/*  788 */             if (next.animation.hasTimeline(id)) { next = next.mixingTo; continue; }
/*  789 */              if (next.mixDuration > 0.0F) {
/*  790 */               timelineMode[i] = 3;
/*  791 */               timelineHoldMix[i] = next;
/*      */               
/*      */               break;
/*      */             } 
/*      */           } 
/*  796 */           timelineMode[i] = 2;
/*      */           break; }
/*      */       
/*      */       } 
/*      */     } 
/*      */   }
/*      */   public TrackEntry getCurrent(int trackIndex) {
/*  803 */     if (trackIndex < 0) throw new IllegalArgumentException("trackIndex must be >= 0."); 
/*  804 */     if (trackIndex >= this.tracks.size) return null; 
/*  805 */     return (TrackEntry)this.tracks.get(trackIndex);
/*      */   }
/*      */ 
/*      */   
/*      */   public void addListener(AnimationStateListener listener) {
/*  810 */     if (listener == null) throw new IllegalArgumentException("listener cannot be null."); 
/*  811 */     this.listeners.add(listener);
/*      */   }
/*      */ 
/*      */   
/*      */   public void removeListener(AnimationStateListener listener) {
/*  816 */     this.listeners.removeValue(listener, true);
/*      */   }
/*      */ 
/*      */   
/*      */   public void clearListeners() {
/*  821 */     this.listeners.clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearListenerNotifications() {
/*  828 */     this.queue.clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public float getTimeScale() {
/*  836 */     return this.timeScale;
/*      */   }
/*      */   
/*      */   public void setTimeScale(float timeScale) {
/*  840 */     this.timeScale = timeScale;
/*      */   }
/*      */ 
/*      */   
/*      */   public AnimationStateData getData() {
/*  845 */     return this.data;
/*      */   }
/*      */   
/*      */   public void setData(AnimationStateData data) {
/*  849 */     if (data == null) throw new IllegalArgumentException("data cannot be null."); 
/*  850 */     this.data = data;
/*      */   }
/*      */ 
/*      */   
/*      */   public Array<TrackEntry> getTracks() {
/*  855 */     return this.tracks;
/*      */   }
/*      */   
/*      */   public String toString() {
/*  859 */     StringBuilder buffer = new StringBuilder(64);
/*  860 */     for (int i = 0, n = this.tracks.size; i < n; i++) {
/*  861 */       TrackEntry entry = (TrackEntry)this.tracks.get(i);
/*  862 */       if (entry != null) {
/*  863 */         if (buffer.length() > 0) buffer.append(", "); 
/*  864 */         buffer.append(entry.toString());
/*      */       } 
/*  866 */     }  if (buffer.length() == 0) return "<none>"; 
/*  867 */     return buffer.toString();
/*      */   }
/*      */   
/*      */   public static class TrackEntry implements Pool.Poolable { Animation animation;
/*      */     TrackEntry next;
/*      */     TrackEntry mixingFrom;
/*      */     TrackEntry mixingTo;
/*      */     AnimationState.AnimationStateListener listener;
/*      */     int trackIndex;
/*      */     boolean loop;
/*      */     boolean holdPrevious;
/*      */     float eventThreshold;
/*      */     float attachmentThreshold;
/*      */     float drawOrderThreshold;
/*      */     float animationStart;
/*      */     float animationEnd;
/*  883 */     Animation.MixBlend mixBlend = Animation.MixBlend.replace; float animationLast; float nextAnimationLast; float delay; float trackTime; float trackLast;
/*      */     float nextTrackLast;
/*  885 */     final IntArray timelineMode = new IntArray(); float trackEnd; float timeScale; float alpha; float mixTime; float mixDuration; float interruptAlpha; float totalAlpha;
/*  886 */     final Array<TrackEntry> timelineHoldMix = new Array();
/*  887 */     final FloatArray timelinesRotation = new FloatArray();
/*      */     
/*      */     public void reset() {
/*  890 */       this.next = null;
/*  891 */       this.mixingFrom = null;
/*  892 */       this.mixingTo = null;
/*  893 */       this.animation = null;
/*  894 */       this.listener = null;
/*  895 */       this.timelineMode.clear();
/*  896 */       this.timelineHoldMix.clear();
/*  897 */       this.timelinesRotation.clear();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getTrackIndex() {
/*  904 */       return this.trackIndex;
/*      */     }
/*      */ 
/*      */     
/*      */     public Animation getAnimation() {
/*  909 */       return this.animation;
/*      */     }
/*      */     
/*      */     public void setAnimation(Animation animation) {
/*  913 */       if (animation == null) throw new IllegalArgumentException("animation cannot be null."); 
/*  914 */       this.animation = animation;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean getLoop() {
/*  920 */       return this.loop;
/*      */     }
/*      */     
/*      */     public void setLoop(boolean loop) {
/*  924 */       this.loop = loop;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public float getDelay() {
/*  934 */       return this.delay;
/*      */     }
/*      */     
/*      */     public void setDelay(float delay) {
/*  938 */       this.delay = delay;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public float getTrackTime() {
/*  945 */       return this.trackTime;
/*      */     }
/*      */     
/*      */     public void setTrackTime(float trackTime) {
/*  949 */       this.trackTime = trackTime;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public float getTrackEnd() {
/*  960 */       return this.trackEnd;
/*      */     }
/*      */     
/*      */     public void setTrackEnd(float trackEnd) {
/*  964 */       this.trackEnd = trackEnd;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public float getAnimationStart() {
/*  972 */       return this.animationStart;
/*      */     }
/*      */     
/*      */     public void setAnimationStart(float animationStart) {
/*  976 */       this.animationStart = animationStart;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public float getAnimationEnd() {
/*  982 */       return this.animationEnd;
/*      */     }
/*      */     
/*      */     public void setAnimationEnd(float animationEnd) {
/*  986 */       this.animationEnd = animationEnd;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public float getAnimationLast() {
/*  994 */       return this.animationLast;
/*      */     }
/*      */     
/*      */     public void setAnimationLast(float animationLast) {
/*  998 */       this.animationLast = animationLast;
/*  999 */       this.nextAnimationLast = animationLast;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public float getAnimationTime() {
/* 1006 */       if (this.loop) {
/* 1007 */         float duration = this.animationEnd - this.animationStart;
/* 1008 */         if (duration == 0.0F) return this.animationStart; 
/* 1009 */         return this.trackTime % duration + this.animationStart;
/*      */       } 
/* 1011 */       return Math.min(this.trackTime + this.animationStart, this.animationEnd);
/*      */     }
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
/*      */     public float getTimeScale() {
/* 1026 */       return this.timeScale;
/*      */     }
/*      */     
/*      */     public void setTimeScale(float timeScale) {
/* 1030 */       this.timeScale = timeScale;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public AnimationState.AnimationStateListener getListener() {
/* 1038 */       return this.listener;
/*      */     }
/*      */ 
/*      */     
/*      */     public void setListener(AnimationState.AnimationStateListener listener) {
/* 1043 */       this.listener = listener;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public float getAlpha() {
/* 1052 */       return this.alpha;
/*      */     }
/*      */     
/*      */     public void setAlpha(float alpha) {
/* 1056 */       this.alpha = alpha;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public float getEventThreshold() {
/* 1063 */       return this.eventThreshold;
/*      */     }
/*      */     
/*      */     public void setEventThreshold(float eventThreshold) {
/* 1067 */       this.eventThreshold = eventThreshold;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public float getAttachmentThreshold() {
/* 1074 */       return this.attachmentThreshold;
/*      */     }
/*      */     
/*      */     public void setAttachmentThreshold(float attachmentThreshold) {
/* 1078 */       this.attachmentThreshold = attachmentThreshold;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public float getDrawOrderThreshold() {
/* 1085 */       return this.drawOrderThreshold;
/*      */     }
/*      */     
/*      */     public void setDrawOrderThreshold(float drawOrderThreshold) {
/* 1089 */       this.drawOrderThreshold = drawOrderThreshold;
/*      */     }
/*      */ 
/*      */     
/*      */     public TrackEntry getNext() {
/* 1094 */       return this.next;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isComplete() {
/* 1101 */       return (this.trackTime >= this.animationEnd - this.animationStart);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public float getMixTime() {
/* 1107 */       return this.mixTime;
/*      */     }
/*      */     
/*      */     public void setMixTime(float mixTime) {
/* 1111 */       this.mixTime = mixTime;
/*      */     }
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
/*      */     public float getMixDuration() {
/* 1128 */       return this.mixDuration;
/*      */     }
/*      */     
/*      */     public void setMixDuration(float mixDuration) {
/* 1132 */       this.mixDuration = mixDuration;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Animation.MixBlend getMixBlend() {
/* 1142 */       return this.mixBlend;
/*      */     }
/*      */     
/*      */     public void setMixBlend(Animation.MixBlend mixBlend) {
/* 1146 */       if (mixBlend == null) throw new IllegalArgumentException("mixBlend cannot be null."); 
/* 1147 */       this.mixBlend = mixBlend;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public TrackEntry getMixingFrom() {
/* 1153 */       return this.mixingFrom;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public TrackEntry getMixingTo() {
/* 1159 */       return this.mixingTo;
/*      */     }
/*      */     
/*      */     public void setHoldPrevious(boolean holdPrevious) {
/* 1163 */       this.holdPrevious = holdPrevious;
/*      */     }
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
/*      */     public boolean getHoldPrevious() {
/* 1178 */       return this.holdPrevious;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void resetRotationDirections() {
/* 1189 */       this.timelinesRotation.clear();
/*      */     }
/*      */     
/*      */     public String toString() {
/* 1193 */       return (this.animation == null) ? "<none>" : this.animation.name;
/*      */     } }
/*      */ 
/*      */   
/*      */   class EventQueue {
/* 1198 */     private final Array objects = new Array();
/*      */     boolean drainDisabled;
/*      */     
/*      */     void start(AnimationState.TrackEntry entry) {
/* 1202 */       this.objects.add(start);
/* 1203 */       this.objects.add(entry);
/* 1204 */       AnimationState.this.animationsChanged = true;
/*      */     }
/*      */     
/*      */     void interrupt(AnimationState.TrackEntry entry) {
/* 1208 */       this.objects.add(interrupt);
/* 1209 */       this.objects.add(entry);
/*      */     }
/*      */     
/*      */     void end(AnimationState.TrackEntry entry) {
/* 1213 */       this.objects.add(AnimationState.EventType.end);
/* 1214 */       this.objects.add(entry);
/* 1215 */       AnimationState.this.animationsChanged = true;
/*      */     }
/*      */     
/*      */     void dispose(AnimationState.TrackEntry entry) {
/* 1219 */       this.objects.add(AnimationState.EventType.dispose);
/* 1220 */       this.objects.add(entry);
/*      */     }
/*      */     
/*      */     void complete(AnimationState.TrackEntry entry) {
/* 1224 */       this.objects.add(AnimationState.EventType.complete);
/* 1225 */       this.objects.add(entry);
/*      */     }
/*      */     
/*      */     void event(AnimationState.TrackEntry entry, Event event) {
/* 1229 */       this.objects.add(AnimationState.EventType.event);
/* 1230 */       this.objects.add(entry);
/* 1231 */       this.objects.add(event);
/*      */     }
/*      */     
/*      */     void drain() {
/* 1235 */       if (this.drainDisabled)
/* 1236 */         return;  this.drainDisabled = true;
/*      */       
/* 1238 */       Array objects = this.objects;
/* 1239 */       Array<AnimationState.AnimationStateListener> listeners = AnimationState.this.listeners;
/* 1240 */       for (int i = 0; i < objects.size; i += 2) {
/* 1241 */         int ii; Event event; int j; AnimationState.EventType type = (AnimationState.EventType)objects.get(i);
/* 1242 */         AnimationState.TrackEntry entry = (AnimationState.TrackEntry)objects.get(i + 1);
/* 1243 */         switch (type) {
/*      */           case start:
/* 1245 */             if (entry.listener != null) entry.listener.start(entry); 
/* 1246 */             for (ii = 0; ii < listeners.size; ii++)
/* 1247 */               ((AnimationState.AnimationStateListener)listeners.get(ii)).start(entry); 
/*      */             break;
/*      */           case interrupt:
/* 1250 */             if (entry.listener != null) entry.listener.interrupt(entry); 
/* 1251 */             for (ii = 0; ii < listeners.size; ii++)
/* 1252 */               ((AnimationState.AnimationStateListener)listeners.get(ii)).interrupt(entry); 
/*      */             break;
/*      */           case end:
/* 1255 */             if (entry.listener != null) entry.listener.end(entry); 
/* 1256 */             for (ii = 0; ii < listeners.size; ii++) {
/* 1257 */               ((AnimationState.AnimationStateListener)listeners.get(ii)).end(entry);
/*      */             }
/*      */           case dispose:
/* 1260 */             if (entry.listener != null) entry.listener.dispose(entry); 
/* 1261 */             for (ii = 0; ii < listeners.size; ii++)
/* 1262 */               ((AnimationState.AnimationStateListener)listeners.get(ii)).dispose(entry); 
/* 1263 */             AnimationState.this.trackEntryPool.free(entry);
/*      */             break;
/*      */           case complete:
/* 1266 */             if (entry.listener != null) entry.listener.complete(entry); 
/* 1267 */             for (ii = 0; ii < listeners.size; ii++)
/* 1268 */               ((AnimationState.AnimationStateListener)listeners.get(ii)).complete(entry); 
/*      */             break;
/*      */           case event:
/* 1271 */             event = (Event)objects.get(i++ + 2);
/* 1272 */             if (entry.listener != null) entry.listener.event(entry, event); 
/* 1273 */             for (j = 0; j < listeners.size; j++)
/* 1274 */               ((AnimationState.AnimationStateListener)listeners.get(j)).event(entry, event); 
/*      */             break;
/*      */         } 
/*      */       } 
/* 1278 */       clear();
/*      */       
/* 1280 */       this.drainDisabled = false;
/*      */     }
/*      */     
/*      */     void clear() {
/* 1284 */       this.objects.clear();
/*      */     }
/*      */   }
/*      */   
/*      */   public enum EventType {
/* 1289 */     start, interrupt, end, dispose, complete, event;
/*      */   }
/*      */   
/*      */   public static abstract class AnimationStateAdapter implements AnimationStateListener {
/*      */     public void start(AnimationState.TrackEntry entry) {}
/*      */     
/*      */     public void interrupt(AnimationState.TrackEntry entry) {}
/*      */     
/*      */     public void end(AnimationState.TrackEntry entry) {}
/*      */     
/*      */     public void dispose(AnimationState.TrackEntry entry) {}
/*      */     
/*      */     public void complete(AnimationState.TrackEntry entry) {}
/*      */     
/*      */     public void event(AnimationState.TrackEntry entry, Event event) {}
/*      */   }
/*      */   
/*      */   public static interface AnimationStateListener {
/*      */     void start(AnimationState.TrackEntry param1TrackEntry);
/*      */     
/*      */     void interrupt(AnimationState.TrackEntry param1TrackEntry);
/*      */     
/*      */     void end(AnimationState.TrackEntry param1TrackEntry);
/*      */     
/*      */     void dispose(AnimationState.TrackEntry param1TrackEntry);
/*      */     
/*      */     void complete(AnimationState.TrackEntry param1TrackEntry);
/*      */     
/*      */     void event(AnimationState.TrackEntry param1TrackEntry, Event param1Event);
/*      */   }
/*      */ }


/* Location:              E:\steam\steamapps\workshop\content\646570\2779256818\Goldenglow.jar!\com\esotericsoftware\spine38\AnimationState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */