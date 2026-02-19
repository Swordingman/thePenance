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
    // 删除了 UPG_COST
    private static final int TOTAL_DAMAGE = 30;

    public FinaleCatastrophe() {
        super(ID, new CardStats(
                CardColor.CURSE,
                CardType.CURSE,
                CardRarity.SPECIAL,
                CardTarget.ALL,
                COST
        ));
        // 删除原来的 setCostUpgrade(UPG_COST);
        setMagic(TOTAL_DAMAGE);
        setExhaust(true);

        tags.add(PenanceMod.CURSE_OF_WOLVES);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 在这里把 this.upgraded 传给 Action
        addToBot(new CatastropheAction(magicNumber, 0.1f, this.upgraded));
    }

    @Override
    public void triggerWhenDrawn() {
        triggerWolfAutoplay();
    }

    public static class CatastropheAction extends AbstractGameAction {
        private int timesLeft;
        private float interval;
        private float timer;
        private boolean isUpgraded;  // 新增：记录是否升级

        // 构造函数增加 isUpgraded 参数
        public CatastropheAction(int times, float interval, boolean isUpgraded) {
            this.timesLeft = times;
            this.interval = interval;
            this.timer = 0.0f;
            this.actionType = ActionType.DAMAGE;
            this.isUpgraded = isUpgraded;
        }

        @Override
        public void update() {
            timer -= Gdx.graphics.getDeltaTime();
            if (timer <= 0) {
                timer += interval;
                doRandomDamage();
                timesLeft--;
                if (timesLeft <= 0) {
                    this.isDone = true;
                }
            }
        }

        private void doRandomDamage() {
            AbstractPlayer p = AbstractDungeon.player;
            ArrayList<AbstractMonster> validMonsters = new ArrayList<>();

            // 1. 筛选活着的怪物
            for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
                if (!m.isDeadOrEscaped()) {
                    validMonsters.add(m);
                }
            }

            boolean hasPlayer = p != null && !p.isDeadOrEscaped();
            boolean hasMonsters = !validMonsters.isEmpty();

            // 如果场上全空（通常不可能），直接结束
            if (!hasPlayer && !hasMonsters) {
                this.isDone = true;
                return;
            }

            AbstractCreature target = null;

            // 2. 核心概率判定
            if (hasPlayer && hasMonsters) {
                // 如果升级了，打玩家概率30%；未升级则为50%
                float hitPlayerChance = this.isUpgraded ? 0.3f : 0.5f;

                // 生成 0.0 到 1.0 的随机数
                if (AbstractDungeon.cardRandomRng.random() < hitPlayerChance) {
                    target = p; // 打玩家
                } else {
                    // 打怪物（从存活的怪物中随机挑一个）
                    target = validMonsters.get(AbstractDungeon.cardRandomRng.random(validMonsters.size() - 1));
                }
            } else if (hasPlayer) {
                // 只有玩家活着（场上没怪了），全打玩家
                target = p;
            } else {
                // 只有怪物活着，全打怪物
                target = validMonsters.get(AbstractDungeon.cardRandomRng.random(validMonsters.size() - 1));
            }

            // 3. 造成伤害与特效 (这部分保持不变)
            target.damage(new DamageInfo(p, 1, DamageInfo.DamageType.THORNS));

            AbstractDungeon.effectList.add(new FlashAtkImgEffect(
                    target.hb.cX + MathUtils.random(-20f, 20f),
                    target.hb.cY + MathUtils.random(-20f, 20f),
                    AttackEffect.BLUNT_LIGHT
            ));

            if (timesLeft % 5 == 0) {
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.LOW, ScreenShake.ShakeDur.SHORT, false);
            }
        }
    }
}