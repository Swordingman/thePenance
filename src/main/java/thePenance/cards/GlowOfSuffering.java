package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.character.Penance;
import thePenance.character.PenanceDifficultyHelper;
import thePenance.powers.GlowOfSufferingPower;
import thePenance.util.CardStats;

public class GlowOfSuffering extends BaseCard {
    public static final String ID = makeID("GlowOfSuffering");
    private static final int COST = 1;

    // 逻辑数值：100代表1倍，150代表1.5倍
    private int multiplierLogic = 100;

    public GlowOfSuffering() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));
        // 构造函数里不用设 magicNumber 了，反正描述里不显示 !M!
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 直接把 logic 数值传给 Power
        if (p.hasPower(GlowOfSufferingPower.POWER_ID)) {
            GlowOfSufferingPower power = (GlowOfSufferingPower) p.getPower(GlowOfSufferingPower.POWER_ID);
            power.stackPower(multiplierLogic);
        } else {
            // 这里的 magicNumber 已经不重要了，用 multiplierLogic 控制倍率
            addToBot(new ApplyPowerAction(p, p,
                    new GlowOfSufferingPower(p, multiplierLogic), multiplierLogic));
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();

            // --- 核心：根据难度切换“写死”的文本 ---
            if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL) {
                // HELL 难度
                this.multiplierLogic = 150; // 逻辑设为 1.5 倍

                // 文本强制设为 "1.5 倍" 那一句
                this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];

            } else {
                // 普通 难度
                this.multiplierLogic = 200; // 逻辑设为 2.0 倍

                // 文本强制设为 "2 倍" 那一句 (UPGRADE_DESCRIPTION)
                this.rawDescription = cardStrings.UPGRADE_DESCRIPTION;
            }

            initializeDescription(); // 刷新显示
        }
    }
}