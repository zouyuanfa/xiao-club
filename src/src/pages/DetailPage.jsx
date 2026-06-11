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
      <div className="img-detail imgcommon" onClick={goSubmit}>
        <img src={btnImg} className="btn" alt="进入表单" />
      </div>
    </SwiperPage>
  )
}

export default DetailPage
