package com.dyonovan.neotech.client

import com.dyonovan.neotech.client.modelfactory.ModelFactory
import com.dyonovan.neotech.common.CommonProxy
import com.dyonovan.neotech.common.blocks.traits.CoreStates
import com.dyonovan.neotech.managers.BlockManager
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import net.minecraft.client.renderer.block.statemap.StateMap.Builder
import net.minecraftforge.client.model.ModelLoader

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 07, 2015
 */
class ClientProxy extends CommonProxy {
    /**
     * Called during the preInit phase of the mod loading
     *
     * This is where you would register blocks and such
     */
    override def preInit() = {
        ModelLoader.setCustomStateMapper(BlockManager.electricFurnace,
            (new Builder).addPropertiesToIgnore(PropertyRotation.FOUR_WAY.getProperty).addPropertiesToIgnore
                    (BlockManager.electricFurnace.asInstanceOf[CoreStates].PROPERTY_ACTIVE).build())
    }

    /**
     * Called during the init phase of the mod loading
     *
     * Now that the items and such are loaded, use this chance to use them
     */
    override def init() = {
        ModelFactory.register()
    }

    /**
     * Called during the postInit phase of the mod loading
     *
     * Usually used to close things opened to load and check for conditions
     */
    override def postInit() = {}

}
