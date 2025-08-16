// SVG UI组件库入口文件
import SvgButton from './SvgButton.vue'
import SvgInput from './SvgInput.vue'
import SvgCard from './SvgCard.vue'

// 组件集合
const components = {
  SvgButton,
  SvgInput,
  SvgCard
}

// 批量安装组件的方法
const install = (app) => {
  Object.keys(components).forEach(key => {
    app.component(key, components[key])
  })
}

// 组件版本
const version = '1.0.0'

// 支持按需引入
export {
  SvgButton,
  SvgInput,
  SvgCard,
  install,
  version
}

// 支持全量引入
export default {
  install,
  version,
  ...components
}

/**
 * SVG UI 组件库使用说明
 * 
 * 1. 全量引入：
 * import SvgUI from '@/components/SvgUI'
 * app.use(SvgUI)
 * 
 * 2. 按需引入：
 * import { SvgButton, SvgInput } from '@/components/SvgUI'
 * 
 * 3. 单独引入：
 * import SvgButton from '@/components/SvgUI/SvgButton.vue'
 * 
 * 组件特点：
 * - iOS风格设计，金属色调
 * - 完全基于SVG实现，矢量图形
 * - 支持多种尺寸和状态
 * - 流畅的动画效果
 * - 良好的可访问性
 * - 响应式设计
 */