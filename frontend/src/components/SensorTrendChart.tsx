import type { ReadingResponse } from '../api/types'
import { formatDate } from '../utils/format'
import styles from './SensorTrendChart.module.scss'

interface Props {
  /** 오래된 값이 먼저 오도록 정렬해서 넘긴다 (왼쪽이 과거) */
  readings: ReadingResponse[]
  unit: string
  thresholdMin: number | null
  thresholdMax: number | null
}

const WIDTH = 640
const HEIGHT = 200
const PADDING = { top: 16, right: 16, bottom: 24, left: 40 }

const INNER_WIDTH = WIDTH - PADDING.left - PADDING.right
const INNER_HEIGHT = HEIGHT - PADDING.top - PADDING.bottom

/**
 * 센서 측정값 추이 꺾은선.
 * 차트 라이브러리를 넣을 만큼 복잡한 그림이 아니라 좌표만 계산해 SVG 로 그렸다.
 */
export default function SensorTrendChart({ readings, unit, thresholdMin, thresholdMax }: Props) {
  if (readings.length === 0) {
    return <p className={styles.empty}>표시할 측정값이 없습니다.</p>
  }

  const values = readings.map((reading) => reading.value)
  const candidates = [...values, thresholdMin, thresholdMax].filter(
    (value): value is number => value !== null,
  )
  const rawMin = Math.min(...candidates)
  const rawMax = Math.max(...candidates)
  // 값이 전부 같으면 위아래로 여백을 둬서 직선이 가운데 오도록 한다.
  const margin = rawMax === rawMin ? Math.max(Math.abs(rawMax) * 0.1, 1) : (rawMax - rawMin) * 0.1
  const min = rawMin - margin
  const max = rawMax + margin

  const x = (index: number) =>
    readings.length === 1
      ? PADDING.left + INNER_WIDTH / 2
      : PADDING.left + (INNER_WIDTH * index) / (readings.length - 1)

  const y = (value: number) => PADDING.top + INNER_HEIGHT * (1 - (value - min) / (max - min))

  const line = readings.map((reading, index) => `${x(index)},${y(reading.value)}`).join(' ')
  const gridValues = [0, 0.25, 0.5, 0.75, 1].map((ratio) => min + (max - min) * ratio)

  return (
    <svg
      className={styles.chart}
      viewBox={`0 0 ${WIDTH} ${HEIGHT}`}
      role="img"
      aria-label="센서 측정값 추이"
    >
      {gridValues.map((value) => (
        <g key={value}>
          <line
            className={styles.grid}
            x1={PADDING.left}
            x2={WIDTH - PADDING.right}
            y1={y(value)}
            y2={y(value)}
          />
          <text className={styles.axisLabel} x={PADDING.left - 8} y={y(value) + 4} textAnchor="end">
            {value.toFixed(1)}
          </text>
        </g>
      ))}

      {thresholdMin !== null && (
        <line
          className={styles.threshold}
          x1={PADDING.left}
          x2={WIDTH - PADDING.right}
          y1={y(thresholdMin)}
          y2={y(thresholdMin)}
        />
      )}
      {thresholdMax !== null && (
        <line
          className={styles.threshold}
          x1={PADDING.left}
          x2={WIDTH - PADDING.right}
          y1={y(thresholdMax)}
          y2={y(thresholdMax)}
        />
      )}

      {readings.length > 1 && <polyline className={styles.line} points={line} />}

      {readings.map((reading, index) => (
        <circle key={reading.id} className={styles.dot} cx={x(index)} cy={y(reading.value)} r={4}>
          <title>{`${formatDate(reading.measuredAt)} · ${reading.value} ${unit}`}</title>
        </circle>
      ))}

      {readings.map((reading, index) => {
        const isEdge = index === 0 || index === readings.length - 1
        if (!isEdge && readings.length > 2) {
          return null
        }
        return (
          <text
            key={reading.id}
            className={styles.axisLabel}
            x={x(index)}
            y={HEIGHT - 6}
            textAnchor={readings.length === 1 ? 'middle' : index === 0 ? 'start' : 'end'}
          >
            {formatDate(reading.measuredAt)}
          </text>
        )
      })}
    </svg>
  )
}
