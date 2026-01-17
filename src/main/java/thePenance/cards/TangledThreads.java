package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.combat.IronWaveEffect;
import thePenance.PenanceMod;
import thePenance.util.CardStats;

import java.util.ArrayList;
import java.util.Collections;

public class TangledThreads extends BaseCard {

    public static final String ID = PenanceMod.makeID("TangledThreads");
    private static final int COST = -1; // X费

    public TangledThreads() {
        super(ID, new CardStats(
                CardColor.CURSE,
                CardType.CURSE,
                CardRarity.SPECIAL,
                CardTarget.ALL_ENEMY,
                COST
        ));
        setExhaust(true);
        tags.add(PenanceMod.CURSE_OF_WOLVES);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 获取当前的能量 X
        int effectX = EnergyPanel.getCurrentEnergy();

        // 如果是X费卡自动释放，通常会消耗掉这些能量
        if (effectX > 0) {
            p.energy.use(effectX);
        }

        if (effectX > 0) {
            // 执行自定义动作
            addToBot(new TangledThreadsAction(p, effectX, upgraded));
        }
    }

    @Override
    public void triggerWhenDrawn() {
        // 直接调用 BaseCard 里的通用方法
        triggerWolfAutoplay();
    }

    // --- 内部 Action ---
    public static class TangledThreadsAction extends AbstractGameAction {
        private final AbstractPlayer p;
        private final int x;
        private final boolean upgraded;

        public TangledThreadsAction(AbstractPlayer p, int x, boolean upgraded) {
            this.p = p;
            this.x = x;
            this.upgraded = upgraded;
            this.duration = Settings.ACTION_DUR_FAST;
        }

        @Override
        public void update() {
            if (duration == Settings.ACTION_DUR_FAST) {
                // 1. 筛选手牌（排除自己）
                ArrayList<AbstractCard> validCards = new ArrayList<>();
                for (AbstractCard c : p.hand.group) {
                    // 排除本卡自身（虽然此时本卡应该在 limbo 或正在打出中，不在手牌，但以防万一）
                    if (!c.uuid.equals(AbstractDungeon.player.cardInUse.uuid)) {
                        validCards.add(c);
                    }
                }

                // 2. 随机打乱并选取 X 张
                Collections.shuffle(validCards, new java.util.Random(AbstractDungeon.cardRandomRng.randomLong()));
                int cardsToExhaustCount = Math.min(x, validCards.size());

                int totalCost = 0;

                // 3. 消耗卡牌并统计费用
                for (int i = 0; i < cardsToExhaustCount; i++) {
                    AbstractCard c = validCards.get(i);

                    // 统计费用：-1费(X费)通常算0，-2(不能打出)算0，或者按实际数字算
                    // 这里采用 standard logic: 如果 costForTurn >= 0 则加，否则加 0
                    if (c.costForTurn > 0) {
                        totalCost += c.costForTurn;
                    }

                    p.hand.moveToExhaustPile(c);
                }

                // 4. 造成伤害
                // 伤害值 = totalCost
                // 次数 = X (如果升级则是 X + X)
                int hits = upgraded ? (x + x) : x;

                if (totalCost > 0 && hits > 0) {
                    for (int i = 0; i < hits; i++) {
                        // 随机选择一个活着的敌人
                        AbstractMonster target = AbstractDungeon.getMonsters().getRandomMonster(null, true, AbstractDungeon.cardRandomRng);
                        if (target != null) {
                            // 添加视觉特效
                            addToTop(new VFXAction(new IronWaveEffect(p.hb.cX, p.hb.cY, target.hb.cX), 0.1F));

                            // 造成伤害
                            addToTop(new DamageAction(target,
                                    new DamageInfo(p, totalCost, DamageInfo.DamageType.THORNS),
                                    AttackEffect.SLASH_VERTICAL));
                        }
                    }
                }
            }
            isDone = true;
        }
    }
}