package rebelkeithy.mods.atum.artifacts;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import rebelkeithy.mods.atum.artifacts.arrow.EntityArrowExplosive;
import rebelkeithy.mods.atum.artifacts.arrow.EntityArrowVelocity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMonthusBlast extends ItemBow
{
    public static final String[] bowPullIconNameArray = new String[] {"MonthusBlast_pull_0", "MonthusBlast_pull_1", "MonthusBlast_pull_2"};
    Icon[] iconArray;

	public ItemMonthusBlast(int par1) 
	{
		super(par1);
		this.setMaxDamage(650);
	}

    /**
     * Return whether this item is repairable in an anvil.
     */
	@Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack)
    {
        return par2ItemStack.itemID == Item.diamond.itemID;
    }
	
    /**
     * called when the player releases the use item button. Args: itemstack, world, entityplayer, itemInUseCount
     */
	@Override
    public void onPlayerStoppedUsing(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer, int par4)
    {
        int j = this.getMaxItemUseDuration(par1ItemStack) - par4;
        j = (int) (j * 0.667);

        ArrowLooseEvent event = new ArrowLooseEvent(par3EntityPlayer, par1ItemStack, j);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return;
        }
        j = event.charge;

        boolean flag = par3EntityPlayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, par1ItemStack) > 0;

        if (flag || par3EntityPlayer.inventory.hasItem(Item.arrow.itemID))
        {
            float f = (float)j / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;

            if ((double)f < 0.1D)
            {
                return;
            }

            if (f > 1.0F)
            {
                f = 1.0F;
            }

            EntityArrowExplosive entityarrow = new EntityArrowExplosive(par2World, par3EntityPlayer, f * 1.5F);
            entityarrow.setDamage(entityarrow.getDamage() * 1.5F);

            if (f == 1.0F)
            {
                entityarrow.setIsCritical(true);
            }

            int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, par1ItemStack);

            if (k > 0)
            {
                entityarrow.setDamage(entityarrow.getDamage() + (double)k * 0.5D + 0.5D);
            }

            int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, par1ItemStack);

            if (l > 0)
            {
                entityarrow.setKnockbackStrength(l);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, par1ItemStack) > 0)
            {
                entityarrow.setFire(100);
            }

            par1ItemStack.damageItem(1, par3EntityPlayer);
            par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            if (flag)
            {
                entityarrow.canBePickedUp = 2;
            }
            else
            {
                par3EntityPlayer.inventory.consumeInventoryItem(Item.arrow.itemID);
            }

            if (!par2World.isRemote)
            {
                par2World.spawnEntityInWorld(entityarrow);
            }
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(ItemStack par1ItemStack)
    {
        return EnumRarity.rare;
    }

    @SideOnly(Side.CLIENT)

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) 
    {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
        {
            par3List.add(EnumChatFormatting.DARK_PURPLE + "Explosive Shot I: Arrows explode");
            par3List.add(EnumChatFormatting.DARK_PURPLE + "on impact");
        } else {
            par3List.add("Explosive Shot I " + EnumChatFormatting.DARK_GRAY + "[SHIFT]");
        }
    }


    @SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        super.registerIcons(par1IconRegister);
        this.iconArray = new Icon[bowPullIconNameArray.length];

        for (int i = 0; i < this.iconArray.length; ++i)
        {
            this.iconArray[i] = par1IconRegister.registerIcon("Atum:" + bowPullIconNameArray[i]);
        }
    }

    /**
     * Player, Render pass, and item usage sensitive version of getIconIndex.
     *
     * @param stack The item stack to get the icon for. (Usually this, and usingItem will be the same if usingItem is not null)
     * @param renderPass The pass to get the icon for, 0 is default.
     * @param player The player holding the item
     * @param usingItem The item the player is actively using. Can be null if not using anything.
     * @param useRemaining The ticks remaining for the active item.
     * @return The icon index
     */
	@Override
    public Icon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
    	if(usingItem != null)
    	{
	    	int j = getMaxItemUseDuration(stack) - useRemaining;
	
	        if (j >= 27)
	        {
	            return getItemIconForUseDuration(2);
	        }
	
	        if (j > 18)
	        {
	            return getItemIconForUseDuration(1);
	        }
	
	        if (j > 0)
	        {
	            return getItemIconForUseDuration(0);
	        }
    	}
        return getIcon(stack, renderPass);
    }

    @SideOnly(Side.CLIENT)
	@Override
    public Icon getItemIconForUseDuration(int par1)
    {
        return this.iconArray[par1];
    }
}
