import { useState, useRef, useMemo, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'

const initialState = {
  first: true,
  initial: [0, 0],
  start: 0,
  swiping: false,
  xy: [0, 0],
}

/**
 * SwiperPage - 滑动手势容器
 * 向上或向左滑动 → 调用 navto (进入下一页)
 * 向下或向右滑动 → 返回上一页
 */
const SwiperPage = ({ children, navto }) => {
  const navigate = useNavigate()
  const startRef = useRef(null)

  const handleTouchStart = (e) => {
    const touch = e.touches ? e.touches[0] : e
    startRef.current = {
      x: touch.clientX,
      y: touch.clientY,
      time: Date.now(),
    }
  }

  const handleTouchEnd = (e) => {
    if (!startRef.current) return
    const touch = e.changedTouches ? e.changedTouches[0] : e
    const deltaX = touch.clientX - startRef.current.x
    const deltaY = touch.clientY - startRef.current.y
    const elapsed = Date.now() - startRef.current.time

    if (elapsed > 1000) return // 超时不算滑动

    const absX = Math.abs(deltaX)
    const absY = Math.abs(deltaY)

    if (absX < 30 && absY < 30) return // 位移太小不算滑动

    if (absX > absY) {
      // 水平滑动
      if (deltaX < 0 && navto) {
        navto() // 向左滑 → 下一页
      } else if (deltaX > 0) {
        navigate(-1) // 向右滑 → 返回
      }
    } else {
      // 垂直滑动
      if (deltaY < 0 && navto) {
        navto() // 向上滑 → 下一页
      } else if (deltaY > 0) {
        navigate(-1) // 向下滑 → 返回
      }
    }
  }

  return (
    <div
      className="swiper"
      onTouchStart={handleTouchStart}
      onTouchEnd={handleTouchEnd}
      onMouseDown={handleTouchStart}
      onMouseUp={handleTouchEnd}
    >
      {children}
    </div>
  )
}

export default SwiperPage
