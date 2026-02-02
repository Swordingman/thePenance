package thePenance.character;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.spine38.Animation;
import com.esotericsoftware.spine38.AnimationState;
import com.esotericsoftware.spine38.SkeletonData;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import thePenance.powers.JudgementPower;
import thePenance.powers.ThornAuraPower;

public class PenanceAnimHelper {

    // --- 内部枚举：定义形态优先级 ---
    private enum FormState {
        NORMAL,
        STR_1,   // [新增] 优先级：低
        THORN_2, // 优先级：中
        JUDGE_3  // 优先级：高
    }

    private AnimationState state;
    private SkeletonData data;
    private String currentIdleAnim = "Idle";
    private float animationLockTimer = 0.0f;

    // --- 动画名称常量 ---
    private static final String IDLE_NORMAL = "Idle";
    private static final String ATTACK_NORMAL = "Attack";

    // [新增] Skill 1 (力量)
    private static final String IDLE_STR_1 = "Skill_1_Idle";
    private static final String ATTACK_STR_1 = "Skill_1_Attack";

    // Skill 2 (荆棘)
    private static final String IDLE_THORN_2 = "Skill_2_Loop";
    // Skill 2 无攻击动画

    // Skill 3 (裁决+力量)
    private static final String IDLE_JUDGE_3 = "Skill_3_Idle";
    private static final String ATTACK_JUDGE_3 = "Skill_3_Attack";

    // --- 阈值常量 ---
    private static final int JUDGE_THRESHOLD = 30;
    private static final int JUDGE_STR_REQ = 10; // 配合裁决需要的力量
    private static final int THORN_THRESHOLD = 20;
    private static final int STR_ONLY_THRESHOLD = 6; // [新增] 单独力量形态阈值

    public PenanceAnimHelper(AnimationState state, SkeletonData data) {
        this.state = state;
        this.data = data;
    }

    public void update() {
        if (AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) return;
        if (AbstractDungeon.player == null || AbstractDungeon.player.isDead) return;
        if (AbstractDungeon.getMonsters() == null || AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            return;
        }

        float dt = Gdx.graphics.getDeltaTime();

        if (animationLockTimer > 0) {
            animationLockTimer -= dt;
        }

        if (animationLockTimer <= 0) {
            updateIdleStance();
        }
    }

    public void playAttack() {
        FormState currentForm = getCurrentForm();

        String attackToPlay;
        String returnToIdle;

        switch (currentForm) {
            case JUDGE_3:
                attackToPlay = ATTACK_JUDGE_3;
                returnToIdle = IDLE_JUDGE_3;
                break;
            case THORN_2:
                // Skill 2 没有攻击动画，保持不动
                return;
            case STR_1: // [新增] Skill 1 攻击逻辑
                attackToPlay = ATTACK_STR_1;
                returnToIdle = IDLE_STR_1;
                break;
            case NORMAL:
            default:
                attackToPlay = ATTACK_NORMAL;
                returnToIdle = IDLE_NORMAL;
                break;
        }

        state.setAnimation(0, attackToPlay, false);
        state.addAnimation(0, returnToIdle, true, 0f);

        this.currentIdleAnim = returnToIdle;

        Animation animObj = data.findAnimation(attackToPlay);
        if (animObj != null) {
            this.animationLockTimer = animObj.getDuration();
        } else {
            this.animationLockTimer = 1.0f;
        }
    }

    private void updateIdleStance() {
        String targetIdle = getIdleAnimForForm(getCurrentForm());
        if (!currentIdleAnim.equals(targetIdle)) {
            state.setAnimation(0, targetIdle, true);
            currentIdleAnim = targetIdle;
        }
    }

    private String getIdleAnimForForm(FormState form) {
        switch (form) {
            case JUDGE_3: return IDLE_JUDGE_3;
            case THORN_2: return IDLE_THORN_2;
            case STR_1:   return IDLE_STR_1; // [新增]
            default:      return IDLE_NORMAL;
        }
    }

    /**
     * [核心逻辑] 计算当前形态优先级
     * 1. 裁决(>30) + 力量(>10) -> Skill 3
     * 2. 荆棘(>20)            -> Skill 2
     * 3. 力量(>6)             -> Skill 1
     * 4. 默认                 -> Normal
     */
    private FormState getCurrentForm() {
        AbstractPlayer p = AbstractDungeon.player;
        if (p == null) return FormState.NORMAL;

        boolean hasJudge = p.hasPower(JudgementPower.POWER_ID);
        boolean hasStr = p.hasPower(StrengthPower.POWER_ID);
        boolean hasThorn = p.hasPower(ThornAuraPower.POWER_ID);

        // 1. 优先级高：Skill 3
        if (hasJudge && hasStr) {
            if (p.getPower(JudgementPower.POWER_ID).amount >= JUDGE_THRESHOLD &&
                    p.getPower(StrengthPower.POWER_ID).amount >= JUDGE_STR_REQ) {
                return FormState.JUDGE_3;
            }
        }

        // 2. 优先级中：Skill 2
        if (hasThorn) {
            if (p.getPower(ThornAuraPower.POWER_ID).amount >= THORN_THRESHOLD) {
                return FormState.THORN_2;
            }
        }

        // 3. 优先级低：Skill 1 [新增]
        if (hasStr) {
            if (p.getPower(StrengthPower.POWER_ID).amount >= STR_ONLY_THRESHOLD) {
                return FormState.STR_1;
            }
        }

        // 4. 默认
        return FormState.NORMAL;
    }

    public void reset() {
        this.animationLockTimer = 0.0F;
        this.currentIdleAnim = IDLE_NORMAL;
        state.setAnimation(0, IDLE_NORMAL, true);
    }
}