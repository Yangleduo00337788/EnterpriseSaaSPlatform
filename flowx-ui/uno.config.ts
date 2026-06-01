import {
  defineConfig,
  presetUno,
  presetAttributify,
  presetIcons,
} from 'unocss'
import transformerDirectives from '@unocss/transformer-directives'

export default defineConfig({
  presets: [
    presetUno(),
    presetAttributify(),
    presetIcons({
      scale: 1.2,
      warn: true,
    }),
  ],
  transformers: [
    transformerDirectives(),
  ],
  shortcuts: {
    'flex-center': 'flex items-center justify-center',
    'flex-between': 'flex items-center justify-between',
    'flex-col-center': 'flex flex-col items-center justify-center',
  },
  theme: {
    colors: {
      primary: '#18a058',
      info: '#2080f0',
      success: '#18a058',
      warning: '#f0a020',
      error: '#d03050',
    },
  },
})
