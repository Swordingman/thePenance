package thePenance.cards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import thePenance.PenanceMod;
import thePenance.util.CardStats;
import java.util.ArrayList;

public class FinaleCatastrophe extends BaseCard {

    public static final String ID = makeID("FinaleCatastrophe");
    private static final int COST = 1;
    private static final int UPG_COST = 0;
    private static final int TOTAL_DAMAGE = 30;

    public FinaleCatastrophe() {
        super(ID, new CardStats(
                CardColor.CURSE,
                CardType.CURSE,
                CardRarity.SPECIAL,
                CardTarget.ALL,
                COST
        ));
        setCostUpgrade(UPG_COST);
        setMagic(TOTAL_DAMAGE);
        setExhaust(true);

        tags.add(PenanceMod.CURSE_OF_WOLVES);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 传入总次数，以及每次打击的间隔时间 (0.1秒一次，30次大约持续3秒)
        addToBot(new CatastropheAction(magicNumber, 0.1f));
    }

    @Override
    public void triggerWhenDrawn() {
        // 直接调用 BaseCard 里的通用方法
        triggerWolfAutoplay();
    }

    // --- 内部 Action 类 ---
    public static class CatastropheAction extends AbstractGameAction {
        private int timesLeft;       // 剩余打击次数
        private float interval;      // 每次打击的间隔时间
        private float timer;         // 计时器

        public CatastropheAction(int times, float interval) {
            this.timesLeft = times;
            this.interval = interval;
            this.timer = 0.0f; // 立即开始第一次打击
            this.actionType = ActionType.DAMAGE;
        }

        @Override
        public void update() {
            // 减去流逝的时间
            timer -= Gdx.graphics.getDeltaTime();

            // 如果计时器归零，进行一次打击
            if (timer <= 0) {
                // 重置计时器 (加上 interval)
                timer += interval;

                doRandomDamage();

                // 扣除次数
                timesLeft--;

                // 如果次数用完，标记动作结束
                if (timesLeft <= 0) {
                    this.isDone = true;
                }
            }
        }

        private void doRandomDamage() {
            AbstractPlayer p = AbstractDungeon.player;
            ArrayList<AbstractCreature> targets = new ArrayList<>();

            // 必须每次打击都重新检查目标列表，因为上一次打击可能已经把怪打死了
            if (!p.isDeadOrEscaped()) {
                targets.add(p);
            }
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDeadOrEscaped()) {
                    targets.add(m);
                }
            }

            // 如果场上没有目标了（虽然不太可能，因为有玩家），直接结束
            if (targets.isEmpty()) {
                this.isDone = true;
                return;
            }

            // 随机选一个目标
            AbstractCreature target = targets.get(AbstractDungeon.cardRandomRng.random(targets.size() - 1));

            // 造成1点伤害
            // 使用 THORNS 避免触发荆棘/反伤，也可以改成 HP_LOSS
            target.damage(new DamageInfo(p, 1, DamageInfo.DamageType.THORNS));

            // 特效：根据目标大小微调位置
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(
                    target.hb.cX + MathUtils.random(-20f, 20f),
                    target.hb.cY + MathUtils.random(-20f, 20f),
                    AttackEffect.BLUNT_LIGHT
            ));

            // 可选：每几次攻击震动一下屏幕，增加打击感
            if (timesLeft % 5 == 0) {
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT, false);
            }
        }
    }
}