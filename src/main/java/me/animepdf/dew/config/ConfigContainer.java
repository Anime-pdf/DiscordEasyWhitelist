package me.animepdf.dew.config;

import lombok.Getter;
import me.animepdf.dew.config.serializers.ComponentSerializer;
import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class ConfigContainer {
    private final File dataFolder;

    private GeneralConfig generalConfig;
    private LanguageConfig languageConfig;

    public ConfigContainer(File dataFolder) {
        this.dataFolder = dataFolder;
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    private <T> T loadConfiguration(Class<T> configClass, String fileName, TypeSerializerCollection serializers) {
        File configFile = new File(dataFolder, fileName);
        Path configPath = configFile.toPath();

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(configPath)
                .defaultOptions(opts -> opts.serializers(serializersInner -> serializersInner.registerAll(serializers)))
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .build();

        try {
            // Create file with default content if it doesn't exist
            if (Files.notExists(configPath)) {
                T newInstance = configClass.getDeclaredConstructor().newInstance();
                CommentedConfigurationNode newNode = loader.createNode();
                ObjectMapper.factory().get(configClass).save(newInstance, newNode);
                loader.save(newNode);
                return newInstance;
            }

            CommentedConfigurationNode node = loader.load();
            T configInstance = ObjectMapper.factory().get(configClass).load(node);

            ObjectMapper.factory().get(configClass).save(configInstance, node);
            loader.save(node);

            return configInstance;

        } catch (ConfigurateException error) {
            System.err.println("Error loading configuration: " + fileName);
            System.err.println("Error: " + error.getMessage());
            System.err.println("Error StackTrace: " + error.getMessage());
            error.printStackTrace();
            // Fallback: try to create a default instance if loading fails catastrophically
            try {
                System.err.println("Attempting to return a default instance for " + fileName);
                return configClass.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException roe) {
                throw new RuntimeException("Failed to instantiate default config for " + fileName, roe);
            }
        } catch (ReflectiveOperationException e) { // For getDeclaredConstructor().newInstance()
            throw new RuntimeException("Failed to create instance of config: " + fileName, e);
        }
    }

    private <T> void saveConfiguration(T configInstance, Class<T> configClass, String fileName, TypeSerializerCollection serializers) {
        File configFile = new File(dataFolder, fileName);
        Path configPath = configFile.toPath();

        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(configPath)
                .defaultOptions(opts -> opts.serializers(serializersInner -> serializersInner.registerAll(serializers)))
                .nodeStyle(NodeStyle.BLOCK)
                .indent(2)
                .build();

        try {
            CommentedConfigurationNode node = loader.createNode();
            ObjectMapper.factory().get(configClass).save(configInstance, node);
            loader.save(node);
        } catch (ConfigurateException error) {
            System.err.println("Error saving configuration: " + fileName);
            error.printStackTrace();
        }
    }

    public void loadConfigs() {
        TypeSerializerCollection generalSerializers = TypeSerializerCollection.builder()
                .build();
        generalConfig = loadConfiguration(GeneralConfig.class, "config.yml", generalSerializers);

        TypeSerializerCollection languageSerializers = TypeSerializerCollection.builder()
                .register(Component.class, new ComponentSerializer())
                .build();

        generateLanguageTemplates(languageSerializers);
        languageConfig = loadConfiguration(LanguageConfig.class, "language.yml", languageSerializers);
    }

    private void generateLanguageTemplates(TypeSerializerCollection serializers) {
        File russianFile = new File(dataFolder, "language.russian.yml");
        File englishFile = new File(dataFolder, "language.yml");

        if (!russianFile.exists()) {
            LanguageConfig russianConfig = LanguageConfig.createRussian();
            saveConfiguration(russianConfig, LanguageConfig.class, russianFile.getName(), serializers);
        }

        if (!englishFile.exists()) {
            LanguageConfig englishConfig = LanguageConfig.createEnglish();
            saveConfiguration(englishConfig, LanguageConfig.class, englishFile.getName(), serializers);
        }
    }

    public void reloadConfigs() {
        loadConfigs();
    }
}