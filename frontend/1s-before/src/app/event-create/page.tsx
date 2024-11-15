'use client';
import Header from '@/components/common/Header';
import EventOwnerCreate from '@/components/eventCreate/EventOwnerCreate';
import EventDetailCreate from '@/components/eventCreate/EventDetailCreate';
import { useRouter } from 'next/navigation';
import { useEventOwner } from '@/hooks/eventOwnerHook';
import { useEventCreate } from '@/hooks/eventCreateHook';

export default function EventCreate() {
  const router = useRouter();
  const { handleDetailSubmit } = useEventCreate();
  const { handleUserSubmit } = useEventOwner();

  const handleCreateEvent = async (event: { preventDefault: () => void }) => {
    try {
      await handleDetailSubmit(event);
      await handleUserSubmit(event);

      alert('이벤트가 성공적으로 생성되었습니다.');
      router.push('/');
      sessionStorage.removeItem('formData');
    } catch (error) {
      alert('이벤트 생성 중 오류가 발생했습니다. 다시 시도해 주세요.');
    }
  };

  return (
    <>
      <Header />
      <div className="m-10">
        <h1 className="text-mainColor1 text-3xl font-bold text-center mb-10">
          이벤트 생성하기
        </h1>
        {/* <div className="grid grid-cols-4 gap-8"> */}
        {/* 입력 폼: 2/3 */}
        <div className="border border-inherit p-10 rounded-lg shadow-lg">
          <form className="p-6 max-w-screen-xl mx-auto space-y-4">
            <EventDetailCreate />
          </form>
          <form className="p-6 mx-auto max-w-screen-xl space-y-6">
            <EventOwnerCreate />
          </form>

          <div className="flex space-x-4 justify-center">
            <button
              type="button"
              onClick={() => router.back()}
              className="min-w-40 py-2 px-4 bg-gray-300 rounded text-gray-700"
            >
              이전
            </button>
            <button
              type="button"
              onClick={handleCreateEvent}
              className="min-w-40 py-2 px-4 bg-mainColor1 text-white rounded"
            >
              이벤트 생성하기
            </button>
          </div>
        </div>

        {/* 미리보기: 1/3 */}
        {/* <div className="col-span-1 max-h-[70vh] border border-inherit p-10 rounded-lg shadow-lg">
            <PreviewComponent />
          </div> */}
        {/* </div> */}
      </div>
    </>
  );
}
