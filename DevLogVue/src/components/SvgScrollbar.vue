<template>
  <div class="svg-scrollbar-container" ref="containerRef">
    <div class="content-wrapper" ref="contentRef" @scroll="handleScroll">
      <slot></slot>
    </div>
    
    <div class="scrollbar-track" v-if="showScrollbar">
      <svg 
        class="scrollbar-svg"
        :width="scrollbarWidth"
        :height="containerHeight"
        @mousedown="handleTrackClick"
      >
        <defs>
          <!-- 轨道渐变改为纯色 -->
          <linearGradient id="trackGradient" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" style="stop-color:#f2f2f7;stop-opacity:1" />
            <stop offset="100%" style="stop-color:#f2f2f7;stop-opacity:1" />
          </linearGradient>
          
          <!-- 滑块渐变改为纯色 -->
          <linearGradient id="thumbGradient" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" style="stop-color:#d1d1d6;stop-opacity:1" />
            <stop offset="100%" style="stop-color:#d1d1d6;stop-opacity:1" />
          </linearGradient>
          
          <filter id="thumbShadow">
            <feDropShadow dx="0" dy="1" stdDeviation="2" flood-opacity="0.1" flood-color="#000000"/>
          </filter>
        </defs>
        
        <rect
          x="2"
          y="0"
          :width="scrollbarWidth - 4"
          :height="containerHeight"
          rx="6"
          ry="6"
          fill="url(#trackGradient)"
          class="scrollbar-track-bg"
        />
        
        <rect
          x="3"
          :y="thumbPosition"
          :width="scrollbarWidth - 6"
          :height="thumbHeight"
          rx="5"
          ry="5"
          fill="url(#thumbGradient)"
          filter="url(#thumbShadow)"
          class="scrollbar-thumb"
          @mousedown="handleThumbMouseDown"
        />
      </svg>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'

const props = defineProps({
  scrollbarWidth: {
    type: Number,
    default: 12
  },
  minThumbHeight: {
    type: Number,
    default: 30
  }
})

const containerRef = ref(null)
const contentRef = ref(null)
const containerHeight = ref(0)
const contentHeight = ref(0)
const scrollTop = ref(0)
const isDragging = ref(false)
const dragStartY = ref(0)
const dragStartScrollTop = ref(0)

const showScrollbar = computed(() => {
  return contentHeight.value > containerHeight.value && containerHeight.value > 0
})

const thumbHeight = computed(() => {
  if (!showScrollbar.value || contentHeight.value === 0) return 0
  const ratio = containerHeight.value / contentHeight.value
  const calculatedHeight = ratio * containerHeight.value
  return Math.max(Math.min(calculatedHeight, containerHeight.value), props.minThumbHeight)
})

const thumbPosition = computed(() => {
  if (!showScrollbar.value) return 0
  
  const maxScroll = contentHeight.value - containerHeight.value
  if (maxScroll <= 0) return 0
  
  const maxThumbPosition = containerHeight.value - thumbHeight.value
  if (maxThumbPosition <= 0) return 0
  
  const scrollRatio = scrollTop.value / maxScroll
  const clampedRatio = Math.max(0, Math.min(1, scrollRatio))
  
  return clampedRatio * maxThumbPosition
})

const handleScroll = (e) => {
  scrollTop.value = e.target.scrollTop
}

const handleTrackClick = (e) => {
  if (isDragging.value) return
  
  const rect = e.currentTarget.getBoundingClientRect()
  const clickY = e.clientY - rect.top
  const thumbCenter = thumbPosition.value + thumbHeight.value / 2
  
  if (clickY < thumbCenter) {
    scrollUp()
  } else {
    scrollDown()
  }
}

const handleThumbMouseDown = (e) => {
  e.stopPropagation()
  isDragging.value = true
  dragStartY.value = e.clientY
  dragStartScrollTop.value = scrollTop.value
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

const handleMouseMove = (e) => {
  if (!isDragging.value) return
  
  const deltaY = e.clientY - dragStartY.value
  const maxThumbPosition = containerHeight.value - thumbHeight.value
  const maxScroll = contentHeight.value - containerHeight.value
  
  const scrollDelta = (deltaY / maxThumbPosition) * maxScroll
  const newScrollTop = Math.max(0, Math.min(maxScroll, dragStartScrollTop.value + scrollDelta))
  
  contentRef.value.scrollTop = newScrollTop
}

const handleMouseUp = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', handleMouseMove)
  document.removeEventListener('mouseup', handleMouseUp)
}

const scrollUp = () => {
  contentRef.value.scrollTop = Math.max(0, scrollTop.value - 100)
}

const scrollDown = () => {
  const maxScroll = contentHeight.value - containerHeight.value
  contentRef.value.scrollTop = Math.min(maxScroll, scrollTop.value + 100)
}

const updateDimensions = () => {
  if (containerRef.value && contentRef.value) {
    const newContainerHeight = containerRef.value.clientHeight
    const newContentHeight = contentRef.value.scrollHeight
    
    // 确保高度值的稳定性
    if (newContainerHeight > 0) {
      containerHeight.value = newContainerHeight
    }
    if (newContentHeight > 0) {
      contentHeight.value = newContentHeight
    }
  }
}

const resizeObserver = new ResizeObserver(() => {
  nextTick(updateDimensions)
})

onMounted(() => {
  updateDimensions()
  if (containerRef.value) {
    resizeObserver.observe(containerRef.value)
  }
  if (contentRef.value) {
    resizeObserver.observe(contentRef.value)
  }
})

onUnmounted(() => {
  resizeObserver.disconnect()
  document.removeEventListener('mousemove', handleMouseMove)
  document.removeEventListener('mouseup', handleMouseUp)
})
</script>

<style scoped>
.svg-scrollbar-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.content-wrapper {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;
  padding-right: 16px;
  box-sizing: border-box;
}

.content-wrapper::-webkit-scrollbar {
  display: none;
}

.scrollbar-track {
  position: absolute;
  top: 0;
  right: 0;
  z-index: 1000;
  pointer-events: auto;
  opacity: 0.6;
  transition: opacity 0.3s ease;
}

.scrollbar-track:hover {
  opacity: 1;
}

.scrollbar-svg {
  display: block;
}

.scrollbar-track-bg {
  transition: fill 0.3s ease;
}

.scrollbar-thumb {
  cursor: pointer;
  transition: all 0.3s ease;
}

.scrollbar-thumb:hover {
  fill: #8e8e93;
  filter: url(#thumbShadow);
}

.scrollbar-thumb:active {
  fill: #6e6e73;
  filter: url(#thumbShadow);
}

@media (max-width: 768px) {
  .scrollbar-track {
    width: 8px;
  }
  
  .content-wrapper {
    padding-right: 10px;
  }
}
</style>