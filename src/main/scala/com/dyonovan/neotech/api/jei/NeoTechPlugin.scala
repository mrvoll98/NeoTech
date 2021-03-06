package com.dyonovan.neotech.api.jei

import java.awt.Rectangle
import java.util

import com.dyonovan.neotech.api.jei.crusher.{CrusherRecipeCategory, CrusherRecipeHandler, CrusherRecipeMaker}
import com.dyonovan.neotech.api.jei.grinder.{GrinderRecipeMaker, GrinderRecipeHandler, GrinderRecipeCategory}
import com.dyonovan.neotech.common.container.misc.ContainerCrafter
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.teambr.bookshelf.client.gui.GuiBase
import mezz.jei.api._
import mezz.jei.api.gui.IAdvancedGuiHandler
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import net.minecraft.item.ItemStack

/**
  * Created by Dyonovan on 1/9/2016.
  */
object NeoTechPlugin {
    var jeiHelpers: IJeiHelpers = _
}

@JEIPlugin
class NeoTechPlugin extends IModPlugin {

    override def onRecipeRegistryAvailable(recipeRegistry: IRecipeRegistry): Unit = { }

    override def register(registry: IModRegistry): Unit = {
        //Crafter Shift Right Click
        registry.getRecipeTransferRegistry.addRecipeTransferHandler(classOf[ContainerCrafter], VanillaRecipeCategoryUid.CRAFTING,  2, 9, 20, 36)

        registry.addRecipeCategories(new CrusherRecipeCategory, new GrinderRecipeCategory)
        registry.addRecipeHandlers(new CrusherRecipeHandler, new GrinderRecipeHandler)

        registry.addRecipes(CrusherRecipeMaker.getRecipes)
        registry.addRecipes(GrinderRecipeMaker.getRecipes)

        registry.addAdvancedGuiHandlers(new IAdvancedGuiHandler[GuiBase[_]] {
            override def getGuiContainerClass: Class[GuiBase[_]] = classOf[GuiBase[_]]

            override def getGuiExtraAreas(t: GuiBase[_]): util.List[Rectangle] = t.getCoveredAreas
        })

        //Descriptions
        registry.addDescription(new ItemStack(BlockManager.grinder), "neotech.grinder.description")
        registry.addDescription(new ItemStack(BlockManager.flushableChest), "neotech.flushableChest.description")
        registry.addDescription(new ItemStack(BlockManager.playerPlate), "neotech.playerPlate.description")
        registry.addDescription(new ItemStack(ItemManager.trashBag), "neotech.trashBag.description")

        //Blacklists
        NeoTechPlugin.jeiHelpers.getItemBlacklist.addItemToBlacklist(new ItemStack(ItemManager.upgradeMBFull))
    }

    override def onItemRegistryAvailable(itemRegistry: IItemRegistry): Unit = { }

    override def onJeiHelpersAvailable(jeiHelpers: IJeiHelpers): Unit = { NeoTechPlugin.jeiHelpers = jeiHelpers }

    override def onRuntimeAvailable(iJeiRuntime: IJeiRuntime): Unit = { }
}
