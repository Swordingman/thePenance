package thePenance.cards;

import basemod.BaseMod;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.character.Penance;
import thePenance.powers.JudgementPower;
import thePenance.powers.ThornAuraPower;
import thePenance.util.CardStats;

import java.util.ArrayList;
import java.util.UUID;

public class PreExecutionPrep extends BaseCard {
    public static final String ID = makeID("PreExecutionPrep");
    private static final int COST = 2;
    private static final int UPG_COST = 1;

    public PreExecutionPrep() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.RARE,
                CardTarget.SELF,
                COST
        ));
        setCostUpgrade(UPG_COST);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 计算需要抽多少张
        int handSize = p.hand.size();
        int drawAmount = BaseMod.MAX_HAND_SIZE - handSize;

        if (drawAmount > 0) {
            // 2. 记录当前手牌的 UUID (唯一标识符)，相当于拍一张快照
            ArrayList<UUID> oldHandSnapshot = new ArrayList<>();
            for (AbstractCard c : p.hand.group) {
                oldHandSnapshot.add(c.uuid);
            }

            // 3. 执行标准抽牌动作 (这会自动处理洗牌、触发遗物等)
            addToBot(new DrawCardAction(p, drawAmount));

            // 4. 执行检测动作 (排队在抽牌之后)
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    for (AbstractCard c : p.hand.group) {
                        // 如果这张卡不在旧快照里，说明它是新抽上来的
                        if (!oldHandSnapshot.contains(c.uuid)) {
                            processCard(c, p);
                        }
                    }
                    this.isDone = true;
                }
            });
        }
    }

    // 处理单张卡牌收益
    private void processCard(AbstractCard c, AbstractPlayer p) {
        switch (c.type) {
            case ATTACK:
                // 攻击 -> 1 裁决
                addToTop(new ApplyPowerAction(p, p, new JudgementPower(p, 1), 1));
                break;
            case SKILL:
                // 技能 -> 1 荆棘
                addToTop(new ApplyPowerAction(p, p, new ThornAuraPower(p, 1), 1));
                break;
            case POWER:
                // 能力 -> 1 力量
                addToTop(new ApplyPowerAction(p, p, new StrengthPower(p, 1), 1));
                break;
            case CURSE:
                // 诅咒 -> 1 能量
                addToTop(new GainEnergyAction(1));
                break;
            default:
                break;
        }
    }
}