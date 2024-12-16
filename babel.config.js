module.exports = {
  presets: ['module:metro-react-native-babel-preset'],
  plugins: [
    'babel-plugin-styled-components',
    [
      'module-resolver',
      {
        root: ['./src'],
        alias: {
          '@screens': './src/screens',
          '@components': './src/components',
          '@apis': './src/apis',
          '@assets': './src/assets',
          '@config': './src/config',
          '@constants': './src/constants',
          '@hooks': './src/hooks',
          '@style': './src/style',
          '@appTypes': './src/types',
          '@utils': './src/utils',
          '@features': './src/features',
        },
      },
    ],
  ],
};
