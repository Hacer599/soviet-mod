package com.soviet;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("soviet")
public class Soviet {
    public static final String MOD_ID = "soviet";

    public Soviet() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.register(com.soviet.client.ClientModEvents.class);
        }
        
        MinecraftForge.EVENT_BUS.register(this);
    }
}
