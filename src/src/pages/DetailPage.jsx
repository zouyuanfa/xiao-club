import { useNavigate } from 'react-router-dom'
import SwiperPage from '../components/SwiperPage'
import btnImg from '../assets/btn-27tt3tdX.jpg'

const DetailPage = () => {
  const navigate = useNavigate()

  const goSubmit = () => {
    navigate('/submit')
  }

  return (
    <SwiperPage>
      <div className="img-detail detail-page">
        <button
          type="button"
          className="detail-action"
          onClick={goSubmit}
          aria-label="进入调研页面"
        >
          <img src={btnImg} alt="进入调研页面" />
        </button>
      </div>
    </SwiperPage>
  )
}

export default DetailPage
