package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.character.Penance;
import thePenance.character.PenanceDifficultyHelper;
import thePenance.powers.CumulativePrecedentPower;
import thePenance.util.CardStats;

public class CumulativePrecedent extends BaseCard {
    public static final String ID = makeID("CumulativePrecedent");
    private static final int COST = 1;

    // 普通难度数值
    private static final int BARRIER_AMT = 3;
    private static final int UPG_BARRIER_AMT = 1;

    // 地狱难度数值
    private static final int HELL_BARRIER_AMT = 1;
    private static final int HELL_BLOCK_AMT = 1;
    private static final int HELL_UPG_BLOCK_AMT = 2;

    public CumulativePrecedent() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.POWER,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        // 1. 根据难度设置数值
        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL) {
            setMagic(HELL_BARRIER_AMT);
            setBlock(HELL_BLOCK_AMT); // 设置 Block 以便 !B! 显示数值

            // 2. 切换为地狱难度的描述 (EXTENDED_DESCRIPTION[0])
            // 假设你的 BaseCard 已经加载了 cardStrings，如果没有，需手动获取
            this.rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
        } else {
            setMagic(BARRIER_AMT, UPG_BARRIER_AMT);
            setBlock(0); // 普通难度不显示 Block

            // 使用默认描述
            this.rawDescription = cardStrings.DESCRIPTION;
        }

        // 3. 必须调用此方法以应用描述变更和变量替换
        initializeDescription();
    }

    // 更正后的 use 方法逻辑，为了解决叠加问题
    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        int barrier = this.magicNumber;
        int blk = this.block;

        boolean alreadyHasPower = p.hasPower(CumulativePrecedentPower.POWER_ID);

        // 标准应用 (这会处理 Barrier 的叠加或新建 Power)
        addToBot(new ApplyPowerAction(p, p, new CumulativePrecedentPower(p, barrier, blk), barrier));

        // 如果玩家之前已经有这个 Power，标准 Action 只会加 Barrier，我们需要补上 Block
        if (alreadyHasPower && blk > 0) {
            addToBot(new AbstractGameAction() {
                @Override
                public void update() {
                    AbstractPower pow = p.getPower(CumulativePrecedentPower.POWER_ID);
                    if (pow instanceof CumulativePrecedentPower) {
                        ((CumulativePrecedentPower) pow).addBlockAmount(blk);
                    }
                    this.isDone = true;
                }
            });
        }
    }

    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();

            // 升级逻辑
            if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL) {
                // 地狱：升级格挡
                this.baseBlock = HELL_UPG_BLOCK_AMT;
                this.upgradedBlock = true;
            } else {
                // 普通：升级屏障
                upgradeMagicNumber(UPG_BARRIER_AMT);
            }

            // 升级后刷新描述（虽然文本结构没变，但数值变了）
            initializeDescription();
        }
    }
}